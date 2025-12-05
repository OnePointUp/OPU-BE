package com.opu.opube.feature.opu.command.infrastructure.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.OpuRandomDrawEvent;
import com.opu.opube.feature.opu.command.domain.repository.OpuRandomDrawEventRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaOpuRandomDrawEventRepository extends OpuRandomDrawEventRepository, JpaRepository<OpuRandomDrawEvent, Long> {
}
