package com.ssafy.controller.mission.heritage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HeritageMissionAnswerResponse {
    private boolean isCorrect;
    private String objectImageUrl;// 오답 시 null
}
