package com.opu.opube.feature.opu.command.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.opu.command.domain.repository.OpuRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class OpuCommandServiceImpl implements OpuCommandService {

    private final OpuRepository opuRepository;

}