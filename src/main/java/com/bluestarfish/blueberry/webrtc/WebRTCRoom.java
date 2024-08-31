package com.bluestarfish.blueberry.webrtc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
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

    public WebRTCRoom(String roomId, MediaPipeline pipeline) {
        this.roomId = roomId;
        this.pipeline = pipeline;
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
        return new UserSession(userName, roomId, session, pipeline);
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

        return newParticipantMsg;
    }

    private void removeParticipant(String name) {
        participants.remove(name);  // 참조값이 없어지니까 알아서 리소스 해제?

        List<String> unnotifiedParticipants = new ArrayList<>();
        JsonObject participantLeftJson = new JsonObject();
        participantLeftJson.addProperty(SOCKET_MESSAGE_ID, PARTICIPANT_LEFT);
        participantLeftJson.addProperty(NAME, name);

        for (UserSession participant : participants.values()) {
            try {
                participant.cancelVideoFrom(name);
                participant.sendMessage(participantLeftJson);
            } catch (IOException e) {
                unnotifiedParticipants.add(participant.getName());
            }
        }
    }

    public void sendParticipantNames(UserSession user) throws IOException {

        JsonArray participantsArray = new JsonArray();
        for (UserSession participant : this.getParticipants()) {
            if (!participant.equals(user)) {
                JsonElement participantName = new JsonPrimitive(participant.getName());
                participantsArray.add(participantName);
            }
        }

        final JsonObject existingParticipantsMsg = new JsonObject();
        existingParticipantsMsg.addProperty("id", "existingParticipants");
        existingParticipantsMsg.add("data", participantsArray);
        log.debug("PARTICIPANT {}: sending a list of {} participants", user.getName(),
                participantsArray.size());
        user.sendMessage(existingParticipantsMsg);
    }

    public Collection<UserSession> getParticipants() {
        return participants.values();
    }

    public UserSession getParticipant(String name) {
        return participants.get(name);
    }

    @Override
    public void close() {
        for (final UserSession user : participants.values()) {
            try {
                user.close();
            } catch (IOException e) {
                log.debug("ROOM {}: Could not invoke close on participant {}", this.roomId, user.getName(),
                        e);
            }
        }

        participants.clear();

        pipeline.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) throws Exception {
                log.trace("ROOM {}: Released Pipeline", WebRTCRoom.this.roomId);
            }

            @Override
            public void onError(Throwable cause) throws Exception {
                log.warn("PARTICIPANT {}: Could not release Pipeline", WebRTCRoom.this.roomId);
            }
        });

        log.debug("Room {} closed", this.roomId);
    }
}
