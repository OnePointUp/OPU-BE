package com.opu.opube.feature.notification.command.infrastructure.repository;

import com.opu.opube.feature.notification.command.domain.aggregate.MemberNotificationSetting;
import com.opu.opube.feature.notification.command.domain.repository.MemberNotificationSettingRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JpaMemberNotificationSettingRepository
        extends JpaRepository<MemberNotificationSetting, Long>,
        MemberNotificationSettingRepository {

    @Override
    @Modifying
    @Query("delete from MemberNotificationSetting m where m.member.id = :memberId")
    void deleteByMemberId(Long memberId);
}
