package com.ssafy.controller.mission.heritage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HeritageMissionResponse {
    private Long problemId;
    private String heritageName;
    private String imageUrl;
    private String description;
    private String objectImageUrl;
    private String content;
    private List<String> choices;  // 객관식 보기
    private String answer;
}