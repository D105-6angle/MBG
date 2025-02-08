package com.ssafy.model.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Social {
    private Long userId;
    private String codeId;
    private String providerId;
    //    private String accessToken;   Access Token은 DB에 저장할 필요 X
    private String refreshToken;

    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
