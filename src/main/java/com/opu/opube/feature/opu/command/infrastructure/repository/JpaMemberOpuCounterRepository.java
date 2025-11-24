package com.opu.opube.feature.opu.command.infrastructure.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.MemberOpuCounter;
import com.opu.opube.feature.opu.command.domain.aggregate.OpuCategory;
import com.opu.opube.feature.opu.command.domain.repository.MemberOpuCounterRepository;
import com.opu.opube.feature.opu.command.domain.repository.OpuCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaMemberOpuCounterRepository extends MemberOpuCounterRepository, JpaRepository<MemberOpuCounter, Long> {
}