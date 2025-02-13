package com.ssafy.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class S3Config {
    private static final Logger logger = LoggerFactory.getLogger(S3Config.class);

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void init() {
        logger.info("AWS Access Key: {}", accessKey);
        logger.info("AWS Secret Key: {}", secretKey != null ? "********" : "NULL");
        logger.info("AWS Region: {}", region);
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
    }
}
