package com.ssafy.controller.Oquiz;

import com.ssafy.model.service.Oquiz.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/random")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;

    @GetMapping("/{missionId}")
    public ResponseEntity<QuizResponse> getRandomQuiz(@PathVariable Long missionId) {
        QuizResponse quiz = quizService.getRandomQuizByMissionId(missionId);
        return ResponseEntity.ok(quiz);
    }
}
