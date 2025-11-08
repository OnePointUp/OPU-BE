package com.opu.opube.feature.opu.query.infrastructure.repository;

import java.util.List;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;

public interface OpuQueryRepository {
    List<Opu> findAll();
}