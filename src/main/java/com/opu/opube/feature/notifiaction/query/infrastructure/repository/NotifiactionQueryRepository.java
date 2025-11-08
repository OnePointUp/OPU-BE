package com.opu.opube.feature.notifiaction.query.infrastructure.repository;

import java.util.List;
import com.opu.opube.feature.notifiaction.command.domain.aggregate.Notifiaction;

public interface NotifiactionQueryRepository {
    List<Notifiaction> findAll();
}