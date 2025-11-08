package com.opu.opube.feature.opu.command.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import com.opu.opube.feature.opu.command.domain.repository.OpuRepository;

public interface JpaOpuRepository extends OpuRepository, JpaRepository<Opu, Long> {
}