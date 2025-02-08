package com.ssafy.controller.notification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponse {
    private String message;  // 실패 시에만 사용
    private NoticeDto notice;
}
