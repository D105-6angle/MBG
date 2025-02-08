package com.ssafy.controller.notification;

import com.ssafy.model.entity.TeacherNotice;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDto {
    private Long noticeId;
    private Long roomId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Boolean status;

    public static NoticeDto from(TeacherNotice entity) {
        return NoticeDto.builder()
                .noticeId(entity.getNoticeId())
                .roomId(entity.getRoomId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .status(entity.getStatus())
                .build();
    }
}
