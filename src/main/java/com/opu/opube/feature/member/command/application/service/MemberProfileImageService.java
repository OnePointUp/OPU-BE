package com.opu.opube.feature.member.command.application.service;

import com.opu.opube.common.s3.S3PresignedUrlService;
import com.opu.opube.common.s3.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberProfileImageService {

    private final S3PresignedUrlService s3PresignedUrlService;

    // PresignedUrl 발급
    public PresignedUrlResponse createPresignedUrl(Long memberId, String extension) {
        String ext = (extension != null && !extension.isBlank()) ? extension : "jpg";

        String folder = "users/%d".formatted(memberId);
        String fileName = "profile_%d".formatted(System.currentTimeMillis());

        return s3PresignedUrlService.generateUploadUrl(folder, fileName, ext);
    }
}