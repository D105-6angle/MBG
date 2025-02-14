package com.ssafy.model.service.amazons3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3Client s3Client;
    private final AwsBasicCredentials credentials;
    private S3Presigner presigner;

    @PostConstruct
    public void init() {
        presigner = S3Presigner.builder().region(s3Client.serviceClientConfiguration().region())
                .credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
    }

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드
    public String uploadFile(MultipartFile file, String dirName) throws IOException {
        String fileName = createFileName(file.getOriginalFilename());
        String fileKey = dirName + "/" + fileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(bucket).key(fileKey)
                .contentType(file.getContentType()).build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return fileKey;
    }

    // Presigned URL 생성
    public String generatePresignedUrl(String fileKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .responseContentDisposition("inline")
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest)
                .build();

        String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
        return presignedUrl;
    }

    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }
}
