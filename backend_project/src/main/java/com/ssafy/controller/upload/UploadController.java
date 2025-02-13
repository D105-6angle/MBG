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
    public ResponseEntity<?> uploadHeritageCard(@RequestPart String cardName, @RequestPart("cardImageFile") MultipartFile cardImageFile) throws IOException, DatabaseOperationException {
        String heritageCardImageS3Key = s3Service.uploadFile(cardImageFile, "heritage/cards");
        uploadService.createHeritageCard(cardName, heritageCardImageS3Key);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/storyCard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "일화 카드 업로드")
    public ResponseEntity<?> uploadStoryCard(@RequestPart String cardName, @RequestPart("cardImageFile") MultipartFile cardImageFile) throws IOException, DatabaseOperationException {
        String storyCardImageS3Key = s3Service.uploadFile(cardImageFile, "story/cards");
        uploadService.createStoryCard(cardName, storyCardImageS3Key);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/heritageProblem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문화재 문제 업로드")
    public ResponseEntity<?> uploadHeritageProblem(@RequestPart("dto") UploadRequest.HeritageProblem requestDto,
                                                   @RequestPart(value = "problemImage") MultipartFile problemImage,
                                                   @RequestPart(value = "objectImage") MultipartFile objectImage) throws IOException, DatabaseOperationException {
        System.out.println("카드 = " + requestDto.toString());

        // 이미지 파일들 S3에 업로드
        String problemImageS3Key = s3Service.uploadFile(problemImage, "heritage/problems");
        String objectImageS3Key = s3Service.uploadFile(objectImage, "heritage/objects");

        // 문제 저장
        uploadService.createHeritageProblem(requestDto, problemImageS3Key, objectImageS3Key);
        return ResponseEntity.ok().build();
    }
}
