package com.opu.opube.feature.notification.command.application.dto.response;

import com.opu.opube.feature.notification.command.domain.aggregate.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "알림 ID", example = "101")
    private Long id;

    @Schema(description = "알림 타입 코드", example = "MORNING")
    private String code;

    @Schema(description = "알림 제목", example = "아침 알림")
    private String title;

    @Schema(description = "알림 본문 메시지", example = "아침 알림 입니다.")
    private String message;

    @Schema(
            description = "해당 알림과 연동된 컨텐츠의 ID",
            example = "55",
            nullable = true
    )
    private Integer linkedContentId;

    @Schema(description = "읽음 여부", example = "false")
    private Boolean read;

    @Schema(description = "알림 생성 시간", example = "2025-01-14T12:31:22")
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