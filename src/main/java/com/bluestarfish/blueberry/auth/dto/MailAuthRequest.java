package com.bluestarfish.blueberry.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailAuthRequest {
    private String email;
    private String code;
}
