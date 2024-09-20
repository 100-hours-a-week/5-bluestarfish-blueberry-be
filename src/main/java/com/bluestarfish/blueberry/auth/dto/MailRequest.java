package com.bluestarfish.blueberry.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequest {
    private String email;
    // FIXME: 요청 타입을 이넘클래스로 대체
    private String type;
}
