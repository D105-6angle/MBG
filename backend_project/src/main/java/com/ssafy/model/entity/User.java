package com.ssafy.model.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {
    private Long userId;
    private String codeId;  // "U001" (학생), "U002" (교사)
    private String name;
    private String nickname;
    private String email;
    private boolean isDeleted;
    private LocalDateTime createdAt;
}
