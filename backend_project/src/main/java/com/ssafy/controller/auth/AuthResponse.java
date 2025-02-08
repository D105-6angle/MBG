package com.ssafy.controller.auth;

import lombok.Builder;
import lombok.Getter;

public class AuthResponse {
    // 로그인, 회원가입 성공했을 때의 응답
    @Getter
    @Builder
    public static class SuccessDto {
        private String message;
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private String nickname;
    }

    @Getter
    @Builder
    public static class SuccessChangeNickname {
        private Long userId;
        private String nickname;
    }

    // 로그인, 회원가입에 실패했을 때의 응답
//    @Getter
//    @Builder
//    public static class FailDto {
//        private int status;
//        private String message;
//        private String error;
//    }
}
