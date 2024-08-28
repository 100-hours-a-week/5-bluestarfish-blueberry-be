package com.bluestarfish.blueberry.config;

import com.bluestarfish.blueberry.webrtc.KurentoHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {
    @Value("${frontend.server.ip}")
    private String frontendServerIp;

    @Value("${ws.chatConnection}")
    private String wsChatConnection;

    @Value("${ws.studyConnection}")
    private String wsStudyConnection;

    @Value("${ws.subscribe}")
    private String subscribe;

    @Value("${ws.publish}")
    private String publish;

    @Value("${kurento.signal}")
    private String signalUrl;

    private KurentoHandler kurentoHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { //websocket 서버에 연결하는 websocket endpoint
        registry.addEndpoint(wsChatConnection).setAllowedOrigins(frontendServerIp) //변경하기
                .withSockJS();

        registry.addEndpoint(wsStudyConnection).setAllowedOrigins(frontendServerIp)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(subscribe); //구독
        registry.setApplicationDestinationPrefixes(publish); //message-handling methods로 라우팅
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(kurentoHandler, signalUrl)
                .setAllowedOriginPatterns(frontendServerIp);
    }
}
