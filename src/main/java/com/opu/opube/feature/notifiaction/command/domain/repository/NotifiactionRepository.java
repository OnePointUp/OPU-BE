package com.opu.opube.feature.notifiaction.command.domain.repository;

import java.util.UUID;
import com.opu.opube.feature.notifiaction.command.domain.aggregate.Notifiaction;

public interface NotifiactionRepository {
    Notifiaction save(Notifiaction notifiaction);
}