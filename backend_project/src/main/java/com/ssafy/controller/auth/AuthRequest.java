package com.ssafy.controller.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

public class AuthRequest {
    // 클라이언트에서 서버로 회원가입 요청할 때 담긴 사용자 정보 DTO
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class UserInfoData {
        private String providerId;
        private String email;
        private String name;
        private String nickname;
    }

    // @SuperBuilder를 사용하면 부모 클래스의 필드들도 포함하여 빌더 패턴 사용 가능
    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    public static class RegistrationUserData extends UserInfoData {
        private Long userId;
        private String codeId;
    }
}
