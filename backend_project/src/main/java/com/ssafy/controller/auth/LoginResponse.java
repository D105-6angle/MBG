package com.ssafy.controller.auth;

import com.ssafy.model.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    // 로그인 성공했을 때의 응답
    @Getter
    @Builder
    public static class SuccessDto {
        private String message;
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private String nickname;
    }

    // 로그인 실패 또는 없는 사용자일 때 응답
    @Getter
    @Builder
    public static class FailDto {
        private int status;
        private String message;
        private String error;
    }
}
