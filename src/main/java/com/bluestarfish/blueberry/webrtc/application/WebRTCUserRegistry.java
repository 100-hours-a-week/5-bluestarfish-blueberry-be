package com.bluestarfish.blueberry.webrtc.application;

import com.bluestarfish.blueberry.webrtc.domain.UserSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebRTCUserRegistry {

    private final ConcurrentHashMap<String, UserSession> usersByName = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, UserSession> usersBySessionId = new ConcurrentHashMap<>();

    public void register(UserSession userSession) {
        usersByName.put(userSession.getName(), userSession);
        usersBySessionId.put(userSession.getSession().getId(), userSession);
    }

    public UserSession getByName(String name) {
        return usersByName.get(name);
    }

    public UserSession getBySession(WebSocketSession webSocketSession) {
        return usersBySessionId.get(webSocketSession.getId());
    }

    public UserSession removeBySession(WebSocketSession webSocketSession) {
        UserSession user = getBySession(webSocketSession);
        usersByName.remove(user.getName());
        usersBySessionId.remove(webSocketSession.getId());
        
        return user;
    }

}

