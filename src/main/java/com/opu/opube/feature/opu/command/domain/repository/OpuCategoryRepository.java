package com.opu.opube.feature.opu.command.domain.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.OpuCategory;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface OpuCategoryRepository extends Repository<OpuCategory, Long> {
    Optional<OpuCategory> getOpuCategoryById(Long categoryId);
}
