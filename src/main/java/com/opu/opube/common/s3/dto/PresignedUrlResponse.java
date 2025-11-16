package com.opu.opube.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PresignedUrlResponse {

    private final String uploadUrl;  // S3에 PUT 할 주소
    private final String finalUrl;   // 클라이언트가 저장해서 쓸 CloudFront 주소
}