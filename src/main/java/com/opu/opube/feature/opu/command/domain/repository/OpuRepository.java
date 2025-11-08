package com.opu.opube.feature.opu.command.domain.repository;

import java.util.UUID;
import com.opu.opube.feature.opu.command.domain.aggregate.Opu;

public interface OpuRepository {
    Opu save(Opu opu);
}