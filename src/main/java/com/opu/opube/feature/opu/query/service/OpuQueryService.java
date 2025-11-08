package com.opu.opube.feature.opu.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.opu.query.infrastructure.repository.OpuQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OpuQueryService {

    private final OpuQueryRepository opuQueryRepository;

}