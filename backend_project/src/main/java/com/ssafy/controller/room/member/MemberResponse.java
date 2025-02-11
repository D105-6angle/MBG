package com.ssafy.controller.room.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private long userId;
    private String name;
    private String nickname;
    private String email;
    private String codeId;
}