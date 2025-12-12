package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.domain.aggregate.NotificationTypeCode;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageFactory {

    public NotificationMessage create(NotificationTypeCode typeCode) {
        return switch (typeCode) {
            case MORNING -> new NotificationMessage(
                    "OPU와 함께 기분 좋은 하루 시작해요 🌤️",
                    "오늘 일정을 확인하고 계획을 세워보아요."
            );
            case EVENING -> new NotificationMessage(
                    "오늘을 잘 마무리하셨나요? 🌙",
                    "오늘 완료하지 못한 일정이 있는지 확인하고 내일 계획을 세워보아요."
            );
            case RANDOM_DRAW -> new NotificationMessage(
                    "오늘의 랜덤 뽑기가 기다리고 있어요!",
                    "OPU를 뽑고 실천하며 오늘도 한 발짝 나아가보아요."
            );
            case ROUTINE -> new NotificationMessage(
                    "루틴을 확인해보세요",
                    null
            );
            default -> new NotificationMessage("알림", "");
        };
    }

    public record NotificationMessage(String title, String message) {}
}