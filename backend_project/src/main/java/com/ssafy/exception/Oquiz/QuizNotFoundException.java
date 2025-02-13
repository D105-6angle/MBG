package com.ssafy.exception.Oquiz;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(Long missionId) {
        super(String.format("미션 ID %d에 해당하는 퀴즈를 찾을 수 없습니다.", missionId));
    }
}