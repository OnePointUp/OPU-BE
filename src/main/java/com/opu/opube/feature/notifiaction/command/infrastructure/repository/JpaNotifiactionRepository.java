package com.opu.opube.feature.notifiaction.command.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opu.opube.feature.notifiaction.command.domain.aggregate.Notifiaction;
import com.opu.opube.feature.notifiaction.command.domain.repository.NotifiactionRepository;

public interface JpaNotifiactionRepository extends NotifiactionRepository, JpaRepository<Notifiaction, Long> {
}