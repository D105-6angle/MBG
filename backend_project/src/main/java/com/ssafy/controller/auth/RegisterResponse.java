package com.ssafy.controller.auth;

import com.ssafy.model.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {
    private int status;
    private String message;
    private Data data;

    @Getter
    @Builder
    public static class Data {
        private Long userId;
        private String codeId;
        private String email;
        private String name;
        private String nickname;
        private String createdAt;
    }

    public static RegisterResponse fromUser(User user) {
        return RegisterResponse.builder()
                .status(200)
                .message("회원가입 성공")
                .data(Data.builder()
                        .userId(user.getUserId())
                        .codeId(user.getCodeId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .nickname(user.getNickname())
                        .createdAt(user.getCreatedAt().toString())
                        .build())
                .build();
    }
}