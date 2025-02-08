package com.ssafy.controller.notification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNoticeRequest {
    private Long roomId;
    private String title;
    private String content;
}
