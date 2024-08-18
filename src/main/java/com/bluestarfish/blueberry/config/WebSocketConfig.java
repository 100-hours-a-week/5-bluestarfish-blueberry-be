package com.bluestarfish.blueberry.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { //websocket 서버에 연결하는 websocket endpoint
        registry.addEndpoint("/ws-chat").setAllowedOrigins("http://localhost:3000") //변경하기
                .withSockJS();

//        registry.addEndpoint("/ws-study").setAllowedOrigins("http://localhost:3000")
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/rooms"); //구독
        registry.setApplicationDestinationPrefixes("/rooms"); //message-handling methods로 라우팅
    }
}
