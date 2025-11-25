package com.opu.opube.feature.opu.command.infrastructure.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.BlockedOpu;
import com.opu.opube.feature.opu.command.domain.repository.BlockedOpuRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaBlockedOpuRepository
        extends BlockedOpuRepository, JpaRepository<BlockedOpu, Long> {
}