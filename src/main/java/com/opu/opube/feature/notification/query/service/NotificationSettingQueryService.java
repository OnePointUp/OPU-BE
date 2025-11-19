package com.opu.opube.feature.notification.query.service;

import com.opu.opube.feature.notification.query.dto.NotificationSettingResponse;
import com.opu.opube.feature.notification.query.infrastructure.repository.NotificationSettingQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSettingQueryService {

    private final NotificationSettingQueryRepository notificationSettingQueryRepository;

    @Transactional(readOnly = true)
    public List<NotificationSettingResponse> getMySettings(Long memberId) {
        return notificationSettingQueryRepository.findMySettings(memberId);
    }
}