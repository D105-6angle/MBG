package com.ssafy.model.service.amazons3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final S3Client s3Client;
    private final AwsBasicCredentials credentials;
    private S3Presigner presigner;
    // 캐시 추가 (만료시간인 1시간보다 적은 50분으로)
    private final Cache<String, String> urlCache = Caffeine.newBuilder().expireAfterWrite(Duration.ofMinutes(50)).maximumSize(1000).recordStats().build();


    @PostConstruct
    public void init() {
        presigner = S3Presigner.builder().region(s3Client.serviceClientConfiguration().region())
                .credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
    }

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file, String dirName) throws IOException {
        // 1. 입력값 로깅
        log.info("=== S3 Upload Debug Info ===");
        log.info("dirName: {}", dirName);
        log.info("file is null: {}", file == null);

        if (file != null) {
            log.info("file.originalFilename: {}", file.getOriginalFilename());
            log.info("file.contentType: {}", file.getContentType());
            log.info("file.size: {}", file.getSize());
        } else {
            throw new IllegalArgumentException("File must not be null");
        }

        if (dirName == null) {
            throw new IllegalArgumentException("Directory name must not be null");
        }

        // 2. S3 설정값 로깅
        log.info("bucket name: {}", bucket);
        log.info("s3Client region: {}", s3Client.serviceClientConfiguration().region());

        // 기존 로직 시작
        String fileName = createFileName(file.getOriginalFilename());
        String fileKey = dirName + "/" + fileName;

        // 3. 생성된 키 로깅
        log.info("generated fileKey: {}", fileKey);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType("image/png")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return fileKey;
    }

    public String generatePresignedUrl(String fileKey) {
        if (fileKey == null) {
            log.error("null 값을 PresignedURL으로 만들려고 시도했습니다.");
            return null;
        }

        // 캐시된 URL 확인
        long startTime = System.currentTimeMillis();
        String cachedUrl = urlCache.getIfPresent(fileKey);
        if (cachedUrl != null) {
            log.info("Cache hit for key: {}", fileKey);
            long endTime = System.currentTimeMillis();
            log.warn("PresignedURL 생성 시간: {}ms, ObjectKey: {}, Cache Status: {}", endTime - startTime, fileKey, urlCache.stats());
            return cachedUrl;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(fileKey)
                .responseContentDisposition("inline").responseContentType("image/png").build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofHours(1))
                .getObjectRequest(getObjectRequest).build();

        String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
        urlCache.put(fileKey, presignedUrl);    // 생성된 URL을 캐시에 저장

        long endTime = System.currentTimeMillis();
        log.warn("PresignedURL 생성 시간: {}ms, ObjectKey: {}, Cache Status: {}", endTime - startTime, fileKey, urlCache.stats());
        return presignedUrl;
    }

//    // Presigned URL 생성
//    public String generatePresignedUrl(String fileKey) {
//        if (fileKey == null) {
//            log.error("null 값을 PresignedURL으로 만들려고 시도했습니다.");
//            return null;
//        }
//
//        long startTime = System.currentTimeMillis();
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucket)
//                .key(fileKey)
//                .responseContentDisposition("inline")
//                .build();
//
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofHours(1))
//                .getObjectRequest(getObjectRequest)
//                .build();
//
//        String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
//        long endTime = System.currentTimeMillis();
//
//        log.warn("PresignedURL 생성 시간: {}ms, ObjectKey: {}", endTime - startTime, fileKey);
//        return presignedUrl;
//    }

    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "_" + originalFileName;
    }
}
