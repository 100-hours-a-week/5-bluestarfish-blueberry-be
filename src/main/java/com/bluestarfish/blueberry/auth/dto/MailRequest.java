package com.bluestarfish.blueberry.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequest {
    private String email;
    private String type;
}
