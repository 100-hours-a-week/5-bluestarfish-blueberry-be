package com.bluestarfish.blueberry.config;

import io.sentry.SentryOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {

    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {

            // 예외의 메세지나 타입을 기반으로 필터링
            if(event.getThrowable() != null) {
              Throwable throwable = event.getThrowable();
              // 잘못된 인자가 입력됐을때, 즉 사용자가 이상한 값을 넣었을때는 Sentry에 기록하지 않는다.
              if(throwable instanceof IllegalArgumentException) {
                  return null; // null로 반환하면 Sentry로 전송되지 않는다.
              }
            }
            return event;
        };
    }
}
