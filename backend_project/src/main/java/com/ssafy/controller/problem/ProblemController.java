package com.ssafy.controller.problem;

import com.ssafy.model.service.amazons3.S3Service;
import com.ssafy.model.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final S3Service s3Service;
    private final ProblemService problemService;

    @PostMapping("/heritage")
    public ResponseEntity<?> uploadHeritageProblem(@RequestPart("dto") ProblemRequest.HeritageRequest requestDto,
                                                   @RequestPart("problemImage") MultipartFile problemImage,
                                                   @RequestPart("objectImage") MultipartFile objectImage) throws IOException {
        // 이미지 파일들 S3에 업로드
        String problemImageS3Key = s3Service.uploadFile(problemImage, "heritage/problems");
        String objectImageS3Key = s3Service.uploadFile(objectImage, "heritage/objects");

        // 문제 저장
        problemService.createHeritageProblem(requestDto, problemImageS3Key, objectImageS3Key);
        return ResponseEntity.ok().build();
    }
}
