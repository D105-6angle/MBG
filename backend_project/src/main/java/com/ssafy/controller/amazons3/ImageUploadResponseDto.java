package com.ssafy.controller.amazons3;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponseDto {
    private String s3Key;           // S3에 저장된 파일 경로
    private String imageUrl;        // Presigned URL
}
