package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.member.command.domain.aggregate.Member;
import com.opu.opube.feature.notification.command.application.dto.request.WebPushSubscribeRequest;
import com.opu.opube.feature.notification.command.application.dto.request.WebPushUnsubscribeRequest;
import com.opu.opube.feature.notification.command.domain.aggregate.WebPushSubscription;
import com.opu.opube.feature.notification.command.domain.repository.WebPushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class WebPushSubscriptionServiceImpl implements WebPushSubscriptionService {

    private final WebPushSubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public void subscribe(Long memberId, WebPushSubscribeRequest req) {

        LocalDateTime expTime;
        if (req.getExpirationTime() != null) {
            expTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(req.getExpirationTime()),
                    ZoneId.systemDefault()
            );
        } else {
            expTime = null;
        }

        subscriptionRepository.findByMemberIdAndEndpoint(memberId, req.getEndpoint())
                .ifPresentOrElse(
                        // 이미 있으면 키/만료 갱신
                        existing -> existing.updateKeys(
                                req.getEndpoint(),
                                req.getP256dh(),
                                req.getAuth(),
                                expTime
                        ),
                        // 없으면 새로 생성
                        () -> {
                            WebPushSubscription s = WebPushSubscription.builder()
                                    .member(Member.builder().id(memberId).build())
                                    .endpoint(req.getEndpoint())
                                    .p256dh(req.getP256dh())
                                    .auth(req.getAuth())
                                    .expirationTime(expTime)
                                    .build();
                            subscriptionRepository.save(s);
                        }
                );
    }

    @Override
    @Transactional
    public void unsubscribe(Long memberId, WebPushUnsubscribeRequest req) {
        subscriptionRepository.deleteByMemberIdAndEndpoint(memberId, req.getEndpoint());
    }
}