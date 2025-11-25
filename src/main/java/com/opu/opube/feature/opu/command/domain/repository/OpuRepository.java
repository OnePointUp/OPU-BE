package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.Opu;

import java.util.Optional;

public interface OpuRepository {
    Opu save(Opu opu);
    Optional<Opu> findById(Long id);
}