package com.opu.opube.feature.notification.command.domain.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.MemberNotificationSetting;
import java.util.Optional;

public interface MemberNotificationSettingRepository {
    Optional<MemberNotificationSetting> findByMemberIdAndNotificationType_Code(Long memberId, String code);
    Optional<MemberNotificationSetting> findByMemberIdAndNotificationTypeId(Long memberId, Long id);
    void deleteByMemberId(Long memberId);
    MemberNotificationSetting save(MemberNotificationSetting setting);
}