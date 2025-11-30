package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.Opu;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OpuRepository {
    Opu save(Opu opu);
    Optional<Opu> findById(Long id);

    @Query("""
    SELECT o
    FROM Opu o
    WHERE o.isShared = TRUE
      AND o.deletedAt IS NULL
      AND o.requiredMinutes = :requiredMinutes
""")
    List<Opu> findSharedByRequiredMinutes(Integer requiredMinutes);
}