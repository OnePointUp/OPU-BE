package com.opu.opube.feature.notification.command.domain.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.MemberNotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberNotificationSettingRepository extends JpaRepository<MemberNotificationSetting, Long> {
    Optional<MemberNotificationSetting> findByMemberIdAndNotificationType_Code(Long memberId, String code);

    Optional<MemberNotificationSetting> findByMemberIdAndNotificationTypeId(Long memberId, Long id);

    @Modifying
    @Query("delete from MemberNotificationSetting m where m.member.id = :memberId")
    void deleteByMemberId(Long memberId);
}