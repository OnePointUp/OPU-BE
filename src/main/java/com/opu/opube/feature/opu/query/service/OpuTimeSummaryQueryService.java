package com.opu.opube.feature.opu.query.service;// feature.opu.query.service

import com.opu.opube.feature.opu.query.dto.request.RequiredMinutesRange;
import com.opu.opube.feature.opu.query.dto.request.RequiredMinutesSummaryResponse;
import com.opu.opube.feature.opu.query.infrastructure.repository.OpuTimeSummaryQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpuTimeSummaryQueryService {

    private final OpuTimeSummaryQueryRepository repository;

    @Transactional(readOnly = true)
    public RequiredMinutesSummaryResponse getAllSummary(Long memberId) {
        Map<RequiredMinutesRange, Long> map =
                repository.findSharedAndMineSummary(memberId);

        return RequiredMinutesSummaryResponse.builder()
                .requiredMinutes(toLabelMap(map))
                .build();
    }

    @Transactional(readOnly = true)
    public RequiredMinutesSummaryResponse getFavoriteSummary(Long memberId) {
        Map<RequiredMinutesRange, Long> map =
                repository.findFavoriteSummary(memberId);

        return RequiredMinutesSummaryResponse.builder()
                .requiredMinutes(toLabelMap(map))
                .build();
    }

    private Map<String, Long> toLabelMap(Map<RequiredMinutesRange, Long> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().label(),
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }
}