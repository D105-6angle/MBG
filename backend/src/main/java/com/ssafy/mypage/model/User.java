package com.ssafy.mypage.model;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long userId;
    private String codeId;
    private String name;
    private String nickname;
    private String email;
    private boolean isDeleted;
    private LocalDateTime createdAt;
}
