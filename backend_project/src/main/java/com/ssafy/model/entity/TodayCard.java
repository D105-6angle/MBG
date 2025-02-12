package com.ssafy.model.entity;

import java.time.LocalDate;
import lombok.*;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayCard {
    private Long todayCardId;
    private Long missionId;
    private Long cardId;
    private LocalDate createdAt;
}
