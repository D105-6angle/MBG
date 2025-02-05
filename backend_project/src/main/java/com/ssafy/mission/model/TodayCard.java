package com.ssafy.mission.model;

import com.ssafy.drawbook.model.Card;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayCard {
    private Long missionId;
    private Long cardId;
    private LocalDate date;

    // 연관
    private MissionPosition missionPosition;
    private Card card;
}
