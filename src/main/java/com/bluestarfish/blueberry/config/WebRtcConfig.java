package com.bluestarfish.blueberry.config;

import com.bluestarfish.blueberry.webrtc.KurentoHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@RequiredArgsConstructor
public class WebRtcConfig implements WebSocketConfigurer {

    private final KurentoHandler kurentoHandler;

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(32768);
        return container;
    }

    @Value("${frontend.server.ip}")
    private String frontendServerIp;

    @Value("${kurento.signal}")
    private String signalUrl;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(kurentoHandler, signalUrl)
                .setAllowedOriginPatterns(frontendServerIp);
    }
}
