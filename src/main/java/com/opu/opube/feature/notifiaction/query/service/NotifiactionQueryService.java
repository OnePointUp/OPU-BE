package com.opu.opube.feature.notifiaction.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.notifiaction.query.infrastructure.repository.NotifiactionQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotifiactionQueryService {

    private final NotifiactionQueryRepository notifiactionQueryRepository;

}