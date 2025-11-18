package com.opu.opube.feature.notification.command.application.dto.response;

import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {
    private Long id;
    private String typeCode;
    private String title;
    private String message;
    private Integer linkedContentId;
    private Boolean read;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .typeCode(n.getNotificationType().getCode())
                .title(n.getTitle())
                .message(n.getMessage())
                .linkedContentId(n.getLinkedContentId())
                .read(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}