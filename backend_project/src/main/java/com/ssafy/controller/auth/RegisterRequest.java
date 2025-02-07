package com.ssafy.controller.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
    private String providerId;
    private String email;
    private String name;
    private String nickname;
}