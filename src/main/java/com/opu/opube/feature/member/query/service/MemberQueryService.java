package com.opu.opube.feature.member.query.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.opu.opube.feature.member.query.infrastructure.repository.MemberQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberQueryRepository memberQueryRepository;

}