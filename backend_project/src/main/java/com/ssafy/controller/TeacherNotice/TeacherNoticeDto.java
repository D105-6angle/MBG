package com.ssafy.controller.TeacherNotice;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherNoticeDto {
    private Long noticeId;
    private String content;
}
