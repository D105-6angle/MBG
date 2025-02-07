package com.ssafy.model.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Social {
    private Long userId;
    private String codeId;
    private String providerId;
    private String accessToken;
    private String refreshToken;
}
