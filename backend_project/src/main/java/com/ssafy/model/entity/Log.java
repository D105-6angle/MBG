package com.ssafy.model.entity;


import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log {
    private Long logId;
    private Long userId;
    private Long cardId;
    private Boolean result;
    private LocalDate timestamp;
}
