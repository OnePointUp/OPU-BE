package com.opu.opube.feature.notification.command.domain.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.MemberNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberNotificationSettingRepository extends JpaRepository<MemberNotificationSetting, Long> {
    Optional<MemberNotificationSetting> findByMemberIdAndNotificationType_Code(Long memberId, String code);
}