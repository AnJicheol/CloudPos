package org.example.cloudpos.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSS3Config {

    @Value("${cloud.aws.region.static}")
    private String region; // ap-northeast-2

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                // credentialsProvider는 생략하면 DefaultCredentialsProvider 사용
                // 로컬: SSO 프로파일
                // EC2: 인스턴스 프로파일(IAM Role)
                .build();
    }
}
