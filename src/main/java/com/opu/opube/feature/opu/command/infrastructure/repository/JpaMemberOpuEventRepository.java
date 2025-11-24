package com.opu.opube.feature.opu.command.infrastructure.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuEvent;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberOpuEventRepository extends MemberOpuEventRepository, JpaRepository<MemberOpuEvent, Long> {
}