package com.ssafy.controller.mission.heritage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HeritageMissionAnswerResponse {
    private boolean isCorrect;
    private String objectImageUrl;// 오답 시 null
}
