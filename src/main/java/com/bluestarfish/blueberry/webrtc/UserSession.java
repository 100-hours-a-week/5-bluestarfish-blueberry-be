package com.bluestarfish.blueberry.webrtc;

import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.exception.UserException;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.*;

@Slf4j
@Getter
public class UserSession implements Closeable {

    private final Long userId;
    private final String name;
    private final String profileImage;
    private final WebSocketSession session;
    private final MediaPipeline pipeline;
    private final String roomName;
    private final WebRtcEndpoint outgoingMedia;
    private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();
    private final UserRepository userRepository;

    public UserSession(
            Long userId,
            String name,
            String profileImage,
            String roomName,
            WebSocketSession session,
            MediaPipeline pipeline,
            UserRepository userRepository
    ) {
        this.userId = userId;
        this.name = name;
        this.profileImage = profileImage;
        this.pipeline = pipeline;
        this.session = session;
        this.roomName = roomName;
        this.userRepository = userRepository;
        this.outgoingMedia = new WebRtcEndpoint.Builder(pipeline).build();

        this.outgoingMedia.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
                JsonObject response = new JsonObject();
                response.addProperty(SOCKET_MESSAGE_ID, ICE_CANDIDATE);
                response.addProperty(NAME, name);
                response.add(CANDIDATE, JsonUtils.toJsonObject(event.getCandidate()));
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            }
        });


    }

    public WebRtcEndpoint getOutgoingWebRtcPeer() {
        return outgoingMedia;
    }

    public void receiveVideoFrom(
            UserSession sender,
            String sdpOffer
    ) throws IOException {
        log.info("'{}'번 방 FROM '{}' TO '{}' offer 도착", roomName, sender.getName(), name);
        log.info("'{}' SDP Offer: {}", sender.getName(), sdpOffer);
        User user = userRepository.findByNicknameAndDeletedAtIsNull(sender.getName()).orElseThrow(
                () -> new UserException("", HttpStatus.NOT_FOUND)
        );

        String ipSdpAnswer = getEndpointForUser(sender).processOffer(sdpOffer);
        JsonObject scParams = new JsonObject();
        scParams.addProperty(SOCKET_MESSAGE_ID, RECEIVE_VIDEO_ANSWER);
        scParams.addProperty("userId", user.getId());
        scParams.addProperty("profileImage", user.getProfileImage());
        scParams.addProperty(NAME, sender.getName());
        scParams.addProperty(SDP_ANSWER, ipSdpAnswer);

        sendMessage(scParams);
        log.info("후보자 수집 시작");
        getEndpointForUser(sender).gatherCandidates();
    }

    private WebRtcEndpoint getEndpointForUser(final UserSession sender) {
        if (sender.getName().equals(name)) {
            return outgoingMedia;
        }

        WebRtcEndpoint incoming = incomingMedia.get(sender.getName());
        if (incoming == null) {
            log.info("WebRTC 엔드포인트 생성 FROM '{}' TO '{}'", this.name, sender.getName());
            incoming = new WebRtcEndpoint.Builder(pipeline).build();

            incoming.addIceCandidateFoundListener(new EventListener<IceCandidateFoundEvent>() {

                @Override
                public void onEvent(IceCandidateFoundEvent event) {
                    JsonObject response = new JsonObject();
                    response.addProperty(SOCKET_MESSAGE_ID, ICE_CANDIDATE);
                    response.addProperty(NAME, sender.getName());
                    response.add(CANDIDATE, JsonUtils.toJsonObject(event.getCandidate()));
                    try {
                        synchronized (session) {
                            session.sendMessage(new TextMessage(response.toString()));
                        }
                    } catch (IOException e) {
                        log.debug(e.getMessage());
                    }
                }
            });

            incomingMedia.put(sender.getName(), incoming);
        }

        sender.getOutgoingWebRtcPeer().connect(incoming);

        return incoming;
    }

    public void cancelVideoFrom(UserSession sender) {
        cancelVideoFrom(sender.getName());
    }

    public void cancelVideoFrom(String senderName) {
        WebRtcEndpoint incoming = incomingMedia.remove(senderName);

        log.info("WebRTC 앤드포인트 제거 FROM '{}' TO '{}'", name, senderName);

        if (incoming != null) {
            incoming.release(new Continuation<Void>() {
                @Override
                public void onSuccess(Void result) {
                    log.info("WebRTC 앤드포인트 제거 완료 FROM '{}' TO '{}'", name, senderName);
                }

                @Override
                public void onError(Throwable cause) {
                    log.warn("WebRTC 앤드포인트 제거 실패 FROM '{}' TO '{}'", name, senderName);
                }
            });
        }
    }

    @Override
    public void close() throws IOException {
        for (String remoteParticipantName : incomingMedia.keySet()) {

            log.info("WebRTC 앤드포인트 제거 FROM '{}' TO '{}'", name, remoteParticipantName);

            WebRtcEndpoint ep = incomingMedia.get(remoteParticipantName);

            ep.release(new Continuation<Void>() {

                @Override
                public void onSuccess(Void result) {
                    log.info("WebRTC 앤드포인트 제거 완료 FROM '{}' TO '{}'", name, remoteParticipantName);
                }

                @Override
                public void onError(Throwable cause) {
                    log.warn("WebRTC 앤드포인트 제거 실패 FROM '{}' TO '{}'", name, remoteParticipantName);
                }
            });
        }

        outgoingMedia.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) {
                log.info("'{}' 나가는 WebRTC 엔드포인트 제거 성공", name);
            }

            @Override
            public void onError(Throwable cause) {
                log.warn("'{}' 나가는 WebRTC 엔드포인트 제거 실패", name);
            }
        });
    }

    public void sendMessage(JsonObject message) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(message.toString()));
        }
    }

    public void addCandidate(IceCandidate candidate, String name) {
        if (this.name.compareTo(name) == 0) {
            outgoingMedia.addIceCandidate(candidate);
        } else {
            WebRtcEndpoint webRtc = incomingMedia.get(name);
            if (webRtc != null) {
                webRtc.addIceCandidate(candidate);
            }
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession) obj;
        boolean eq = name.equals(other.name);
        eq &= roomName.equals(other.roomName);
        return eq;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + name.hashCode();
        result = 31 * result + roomName.hashCode();
        return result;
    }
}
    
