package com.bluestarfish.blueberry.webrtc;

import com.bluestarfish.blueberry.user.entity.User;
import com.bluestarfish.blueberry.user.exception.UserException;
import com.bluestarfish.blueberry.user.repository.UserRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.springframework.http.HttpStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.bluestarfish.blueberry.webrtc.constant.RTCMessage.*;

@Slf4j
@Getter
public class WebRTCRoom implements Closeable {
    private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<>();
    private final MediaPipeline pipeline;
    private final String roomId;
    private final UserRepository userRepository;

    public WebRTCRoom(
            String roomId,
            MediaPipeline pipeline,
            UserRepository userRepository
    ) {
        this.roomId = roomId;
        this.pipeline = pipeline;
        this.userRepository = userRepository;
        log.info("{} 번 방 생성", roomId);
    }

    @PreDestroy
    private void shutdown() {
        this.close();
    }

    public UserSession join(String userName, WebSocketSession session) throws IOException {
        UserSession participant = createNewParticipant(userName, session);
        joinRoom(participant);
        participants.put(participant.getName(), participant);
        sendParticipantNames(participant);

        log.info("'{}'번 방: '{}' 참가", roomId, userName);
        return participant;
    }

    private UserSession createNewParticipant(String userName, WebSocketSession session) {
        User user = userRepository.findByNicknameAndDeletedAtIsNull(userName)
                .orElseThrow(
                        () -> new UserException("", HttpStatus.NOT_FOUND)
                );

        return new UserSession(
                user.getId(),
                userName,
                user.getProfileImage(),
                roomId,
                session,
                pipeline,
                userRepository
        );
    }

    public void leave(UserSession userSession) throws IOException {
        log.info("'{}'번 방 '{}' 님 퇴장", roomId, userSession.getName());
        removeParticipant(userSession.getName());
        userSession.close();
    }

    private void joinRoom(UserSession newParticipant) {
        JsonObject newParticipantMessage = createNewParticipantMessage(newParticipant);

        participants.values()
                .forEach(participant -> {
                    try {
                        participant.sendMessage(newParticipantMessage);
                    } catch (IOException e) {
                        log.debug("'{}'번 방: '{}' 입장 알림 실패", roomId, participant.getName(), e);
                    }
                });
    }

    private JsonObject createNewParticipantMessage(UserSession newParticipant) {
        JsonObject newParticipantMsg = new JsonObject();
        newParticipantMsg.addProperty(SOCKET_MESSAGE_ID, NEW_PARTICIPANT_ARRIVED);
        newParticipantMsg.addProperty(NAME, newParticipant.getName());
        User user = userRepository.findByNicknameAndDeletedAtIsNull(newParticipant.getName())
                .orElseThrow(
                        () -> new UserException("", HttpStatus.NOT_FOUND)
                );

        return newParticipantMsg;
    }

    private void removeParticipant(String name) {
        participants.remove(name);

        List<String> unNotifiedParticipants = new ArrayList<>();
        JsonObject participantLeftJson = new JsonObject();
        participantLeftJson.addProperty(SOCKET_MESSAGE_ID, PARTICIPANT_LEFT);
        participantLeftJson.addProperty(NAME, name);

        for (UserSession participant : participants.values()) {
            try {
                participant.cancelVideoFrom(name);
                participant.sendMessage(participantLeftJson);
            } catch (IOException e) {
                unNotifiedParticipants.add(participant.getName());
            }
        }
    }

    public void sendParticipantNames(UserSession user) throws IOException {
        JsonObject existingParticipantsMsg = new JsonObject();
        existingParticipantsMsg.addProperty(SOCKET_MESSAGE_ID, EXISTING_PATICIPANTS);
        existingParticipantsMsg.add(
                DATA,
                getParticipants().stream()
                        .filter(participant -> !participant.equals(user))
                        .map(participant -> new JsonPrimitive(participant.getName()))
                        .collect(JsonArray::new, JsonArray::add, JsonArray::addAll)
        );
        user.sendMessage(existingParticipantsMsg);
    }

    public Collection<UserSession> getParticipants() {
        return participants.values();
    }

    public void sendCamControl(
            JsonObject jsonMessage,
            UserSession userSession
    ) {
        JsonObject message = new JsonObject();
        message.addProperty(SOCKET_MESSAGE_ID, IS_CAM_ON);
        message.addProperty(SENDER, userSession.getName());
        message.addProperty(IS_CAM_ON, jsonMessage.get(IS_CAM_ON).getAsBoolean());

        participants.values().stream()
                .filter(participant -> !participant.getName().equals(userSession.getName())) // 본인의 이름을 제외
                .forEach(participant -> {
                    try {
                        participant.sendMessage(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void sendMicControl(
            JsonObject jsonMessage,
            UserSession userSession
    ) {
        JsonObject message = new JsonObject();
        message.addProperty(SOCKET_MESSAGE_ID, IS_MIC_ON);
        message.addProperty(SENDER, userSession.getName());
        message.addProperty(IS_MIC_ON, jsonMessage.get(IS_MIC_ON).getAsBoolean());

        participants.values().stream()
                .filter(participant -> !participant.getName().equals(userSession.getName())) // 본인의 이름을 제외
                .forEach(participant -> {
                    try {
                        participant.sendMessage(message);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void close() {
        participants.values()
                .forEach(user -> {
                    try {
                        user.close();
                    } catch (IOException e) {
                        log.debug("'{}'번 방 '{}'님 자원할당 해제 실패", roomId, user.getName(), e);
                    }
                });

        participants.clear();

        pipeline.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) {
                log.info("'{}' 번 방 파이프라인 해제", roomId);
            }

            @Override
            public void onError(Throwable cause) {
                log.warn("'{}'번 방 파이프라인 해제 실패", roomId);
            }
        });

        log.info("'{}'번 방 리소스 클리어", roomId);
    }
}
