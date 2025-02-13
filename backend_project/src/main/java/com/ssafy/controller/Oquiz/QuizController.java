package com.ssafy.controller.Oquiz;

import com.ssafy.exception.Oquiz.QuizNotFoundException;
import com.ssafy.model.service.Oquiz.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/random")
@RequiredArgsConstructor
@Tag(name = "Quiz", description = "Quiz API endpoints")
public class QuizController {
    private final QuizService quizService;

    @Operation(summary = "미션 ID로 해당 일화 퀴즈 조회")
    @ApiResponse(responseCode = "200", description = "Quiz found")
    @ApiResponse(responseCode = "404", description = "Quiz not found")
    @GetMapping("/{missionId}")
    public ResponseEntity<QuizResponse> getRandomQuiz(@PathVariable @Positive Long missionId) {
        try {
            QuizResponse quiz = quizService.getRandomQuizByMissionId(missionId);
            return ResponseEntity.ok(quiz);
        } catch (QuizNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/result/{missionId}")
    public ResponseEntity<QuizResultResponse> submitQuizAnswer(
            @PathVariable Long missionId,
            @RequestBody QuizAnswerRequest request
    ) {
        QuizResultResponse result = quizService.processQuizAnswer(
                request.getUserId(),
                missionId,
                request.getAnswer()
        );
        return ResponseEntity.ok(result);
    }
}