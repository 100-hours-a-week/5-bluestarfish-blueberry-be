package com.bluestarfish.blueberry.config;

import org.kurento.client.KurentoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KurentoConfig {
    @Value("${kurento.url}")
    private String kurentoUrl;
    
    @Bean
    public KurentoClient kurentoClient() {
        return KurentoClient.create(kurentoUrl);
    }
}
