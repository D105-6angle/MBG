package com.ssafy.controller.mission;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreviewDto {
    private String problem;
    private String answer;
    private String script;
    private String message;

    public static PreviewDto heritagePreview(String problem, String answer){
        return PreviewDto.builder()
                .problem(problem)
                .answer(answer)
                .build();
    }

    public static PreviewDto storyPreview(String script){
        return PreviewDto.builder()
                .script(script)
                .build();
    }

    public static PreviewDto teamPreview() {
        return PreviewDto.builder()
                .message("팀 미션입니다.")
                .build();
    }
}
