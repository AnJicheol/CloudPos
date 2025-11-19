package org.example.cloudpos.product.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;
@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, String dirName) {

        String fileName = dirName + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }

        // URL 직접 생성 (region 불필요, 이미 EC2에서 고정)
        return String.format(
                "https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                bucket,
                fileName
        );
    }

    /**
     * S3 URL에서 object key 추출
     * - full URL 이면 prefix 제거하고 key만 리턴
     * - 이미 key만 들어오면 그대로 리턴
     * - null/빈 값이면 null 리턴
     */
    public String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        String prefix = String.format(
                "https://%s.s3.ap-northeast-2.amazonaws.com/",
                bucket
        );

        if (imageUrl.startsWith(prefix)) {
            return imageUrl.substring(prefix.length());
        }

        // 이미 key만 넘어온 경우일 수도 있으니 그대로 사용
        return imageUrl;
    }

    /**
     * S3 객체 삭제
     * - imageUrl 이 full URL이든 key든 모두 처리
     * - null/빈 값이면 아무것도 안 함
     */
    public void delete(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl);

        if (key == null || key.isBlank()) {
            log.warn("S3 삭제 요청 무시 - 유효하지 않은 imageUrl: {}", imageUrl);
            return;
        }

        log.info("S3 삭제 요청 - bucket: {}, key: {}", bucket, key);

        s3Client.deleteObject(b -> b
                .bucket(bucket)
                .key(key)
        );
    }
}



