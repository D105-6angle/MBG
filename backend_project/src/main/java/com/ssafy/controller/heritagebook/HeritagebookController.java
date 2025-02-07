package com.ssafy.controller.heritagebook;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.model.service.heritagebook.HeritagebookService;
import com.ssafy.controller.heritagebook.HeritagebookRequest;
import com.ssafy.controller.heritagebook.HeritagebookResponse;

@RestController
@RequestMapping("/api/users/{userId}/heritagebook")
@RequiredArgsConstructor
@Tag(name = "도감", description = "도감 API")
public class HeritagebookController {

    private final HeritagebookService heritagebookService;

    @Operation(summary = "도감 전체 조회")
    @GetMapping
    public ResponseEntity<HeritagebookResponse.ListResponse> getAllCards(@PathVariable Long userId) {
        return ResponseEntity.ok(heritagebookService.getAllCards(userId));
    }

    @Operation(summary = "도감 카드 상세 조회")
    @GetMapping("/cards/{cardId}")
    public ResponseEntity<?> getCardDetails(
            @PathVariable Long userId, @PathVariable Long cardId) {

        try {
            // 카드를 찾았을 때 상세조회 응답
            return ResponseEntity.ok(heritagebookService.getCardDetails(userId, cardId));

        } catch (IllegalArgumentException e) {
            // 카드 못 찾았을 때의 응답
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(HeritagebookResponse.ErrorDto.builder()
                            .status(404)
                            .message("해당하는 카드가 없습니다.")
                            .error("RESOURCE_NOT_FOUND")
                            .build());
        }
    }
}
