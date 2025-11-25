package com.opu.opube.feature.opu.command.infrastructure.repository;

import com.opu.opube.feature.opu.command.domain.aggregate.FavoriteOpu;
import com.opu.opube.feature.opu.command.domain.repository.FavoriteOpuRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFavoriteOpuRepository extends FavoriteOpuRepository, JpaRepository<FavoriteOpu, Long> {
}
