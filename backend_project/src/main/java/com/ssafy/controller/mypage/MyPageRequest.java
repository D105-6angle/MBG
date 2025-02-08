package com.ssafy.controller.mypage;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class MyPageRequest {
    // 닉네임 변경 요청
    @Getter
    @NoArgsConstructor
    public static class ChangedNickname {
        private String nickname;
    }
}
