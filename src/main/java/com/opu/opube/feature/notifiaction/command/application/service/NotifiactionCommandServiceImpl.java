package com.opu.opube.feature.notifiaction.command.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.notifiaction.command.domain.repository.NotifiactionRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class NotifiactionCommandServiceImpl implements NotifiactionCommandService {

    private final NotifiactionRepository notifiactionRepository;

}