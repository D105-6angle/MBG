package com.ssafy.controller.heritagebook;


import com.ssafy.model.service.amazons3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ssafy.model.service.heritagebook.HeritagebookService;

@RestController
@RequestMapping("/api/users/{userId}/heritagebook")
@RequiredArgsConstructor
@Tag(name = "도감", description = "도감 API")
public class HeritagebookController {
    private final HeritagebookService heritagebookService;
    private final S3Service s3Service;

    @Operation(summary = "도감 전체 조회")
    @GetMapping
    public ResponseEntity<HeritagebookResponse.ListResponse> getAllCards(@PathVariable Long userId) {
        HeritagebookResponse.ListResponse response = heritagebookService.getAllCards(userId);
        for (HeritagebookResponse.DetailResponse detail : response.getCards()) {
            String presignedUrl = s3Service.generatePresignedUrl(detail.getImageUrl());
            detail.setImageUrl(presignedUrl);
        }
        return ResponseEntity.ok(response);
    }


}
