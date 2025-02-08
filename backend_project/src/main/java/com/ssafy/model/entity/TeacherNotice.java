package com.ssafy.model.entity;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherNotice {
    private Long noticeId;
    private Long roomId;
    private String content;
    private LocalDateTime createdAt;
    private String title;
    private Boolean status;  // status 필드 추가
}
