package com.opu.opube.feature.notification.command.application.service;

import com.opu.opube.feature.notification.command.application.dto.request.WebPushSubscribeRequest;
import com.opu.opube.feature.notification.command.application.dto.request.WebPushUnsubscribeRequest;

public interface WebPushSubscriptionService {

    void subscribe(Long memberId, WebPushSubscribeRequest req);

    void unsubscribe(Long memberId, WebPushUnsubscribeRequest req);
}