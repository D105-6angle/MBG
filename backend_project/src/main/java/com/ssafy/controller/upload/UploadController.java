package com.ssafy.controller.upload;

import com.ssafy.exception.common.DatabaseOperationException;
import com.ssafy.model.service.amazons3.S3Service;
import com.ssafy.model.service.upload.UploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Tag(name = "사진 데이터 업로드")
@Slf4j
public class UploadController {
    private final S3Service s3Service;
    private final UploadService uploadService;

    @PostMapping(value = "/heritageCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문화재 카드 업로드")
    public ResponseEntity<?> uploadHeritageCard(@ModelAttribute UploadRequest.Card request) throws IOException, DatabaseOperationException {
        String heritageCardImageS3Key = s3Service.uploadFile(request.getCardImageFile(), "heritage/cards");
        uploadService.createHeritageCard(request.getCardName(), heritageCardImageS3Key);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/storyCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "일화 카드 업로드")
    public ResponseEntity<?> uploadStoryCard(@ModelAttribute UploadRequest.Card request) throws IOException, DatabaseOperationException {
        String storyCardImageS3Key = s3Service.uploadFile(request.getCardImageFile(), "story/cards");
        uploadService.createStoryCard(request.getCardName(), storyCardImageS3Key);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/heritageProblem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문화재 문제 업로드")
    public ResponseEntity<?> uploadHeritageProblem(@ModelAttribute UploadRequest.HeritageProblem request)
            throws IOException, DatabaseOperationException {

        log.debug("Received request: " + request.toString());

        // 이미지 파일들 S3에 업로드
        String problemImageS3Key = s3Service.uploadFile(request.getProblemImage(), "heritage/problems");
        String objectImageS3Key = s3Service.uploadFile(request.getObjectImage(), "heritage/objects");

        // 문제 저장
        uploadService.createHeritageProblem(request, problemImageS3Key, objectImageS3Key);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/StoryProblem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "일화 문제 업로드")
    public ResponseEntity<?> uploadStoryProblem(@ModelAttribute UploadRequest.StoryProblem request)
            throws IOException, DatabaseOperationException {

        log.debug("Received request: " + request.toString());

        String blackIconImageS3Key = s3Service.uploadFile(request.getBlackIconImageFile(), "story/problems");
        String colorIconImageS3Key = s3Service.uploadFile(request.getColorIconImageFile(), "story/problems");

        // 문제 저장
        uploadService.createStoryProblem(request, blackIconImageS3Key, colorIconImageS3Key);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/storyQuiz")
    @Operation(summary = "일화 퀴즈 업로드")
    public ResponseEntity<?> uploadStoryQuiz(@RequestBody UploadRequest.StoryQuiz request)
            throws DatabaseOperationException {
        // 퀴즈 저장
        uploadService.createStoryQuiz(request);
        return ResponseEntity.ok().build();
    }
}
