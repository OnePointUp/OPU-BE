package com.opu.opube.feature.notification.query.service;

import com.opu.opube.feature.member.query.service.MemberQueryService;
import com.opu.opube.feature.notification.query.dto.NotificationSettingListResponse;
import com.opu.opube.feature.notification.query.dto.NotificationSettingResponse;
import com.opu.opube.feature.notification.query.infrastructure.repository.NotificationSettingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSettingQueryService {

    private final NotificationSettingQueryRepository settingQueryRepository;
    private final MemberQueryService memberQueryService;

    public NotificationSettingListResponse getMySettings(Long memberId) {

        Boolean agreed = memberQueryService.getWebPushAgreed(memberId);

        List<NotificationSettingResponse> settings =
                settingQueryRepository.findMySettings(memberId);

        return NotificationSettingListResponse.builder()
                .webPushAgreed(agreed)
                .settings(settings)
                .build();
    }
}