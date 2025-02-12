package com.ssafy.model.entity;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MissionPosition {
    private Long missionId;
    private Long placeId;
    private String codeId;
    private Long cardId;
    private String positionName;
    private String centerPoint;     // 파싱하기 전 담을 문자열 변수
    private String edgePoints;      // 파싱하기 전 담을 문자열 변수

    // 원래 Entity에 없지만 파싱 과정을 위해 어쩔 수 없이 가져오기
    private boolean isCorrect;
}
