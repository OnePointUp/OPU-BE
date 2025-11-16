package com.opu.opube.common.s3;

import com.opu.opube.common.s3.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;


@Service
@RequiredArgsConstructor
public class S3PresignedUrlService {

    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.cloudfront-domain}")
    private String cloudfrontDomain;

    public PresignedUrlResponse generateUploadUrl(String folder, String fileName, String extension) {
        String ext = (extension != null && !extension.isBlank()) ? extension : "jpg";

        String objectKey = "%s/%s.%s".formatted(folder, fileName, ext);

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .contentType("image/" + ext)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(objectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignPutObject(presignRequest).url();
        String finalUrl = "https://%s/%s".formatted(cloudfrontDomain, objectKey);

        return PresignedUrlResponse.builder()
                .uploadUrl(presignedUrl.toString())
                .finalUrl(finalUrl)
                .build();
    }
}