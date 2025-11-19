package com.opu.opube.feature.notification.query.service;

import com.opu.opube.common.dto.PageResponse;
import com.opu.opube.feature.notification.command.application.dto.response.NotificationResponse;
import com.opu.opube.feature.notification.query.infrastructure.repository.NotificationQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;


    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(
            Long memberId,
            boolean onlyUnread,
            Pageable pageable
    ) {
        Page<NotificationResponse> page =
                notificationQueryRepository.findMyNotifications(memberId, onlyUnread, pageable);

        return PageResponse.from(page);
    }


    @Transactional(readOnly = true)
    public long getUnreadCount(Long memberId) {
        return notificationQueryRepository.countUnread(memberId);
    }
}