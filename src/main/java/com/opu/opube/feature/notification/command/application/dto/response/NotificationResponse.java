package com.opu.opube.feature.notification.command.application.dto.response;

import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private String code;
    private String title;
    private String message;
    private Integer linkedContentId;
    private Boolean read;
    private LocalDateTime createdAt;


    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .code(n.getNotificationType().getCode())
                .title(n.getTitle())
                .message(n.getMessage())
                .linkedContentId(n.getLinkedContentId())
                .read(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}