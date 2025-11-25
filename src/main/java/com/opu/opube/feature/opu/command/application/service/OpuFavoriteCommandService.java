package com.opu.opube.feature.opu.command.application.service;

public interface OpuFavoriteCommandService {

    void addFavorite(Long memberId, Long opuId);

    void removeFavorite(Long memberId, Long opuId);
}
