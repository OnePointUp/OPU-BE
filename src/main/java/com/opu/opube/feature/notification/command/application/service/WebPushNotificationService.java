// com.opu.opube.feature.notification.command.application.service.WebPushNotificationService

package com.opu.opube.feature.notification.command.application.service;


import com.opu.opube.common.push.WebPushProperties;
import com.opu.opube.feature.notification.command.domain.aggregate.WebPushSubscription;
import com.opu.opube.feature.notification.command.domain.repository.WebPushSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebPushNotificationService {

    private final WebPushSubscriptionRepository subscriptionRepository;
    private final WebPushProperties vapidProperties;

    private PushService pushService;

    @PostConstruct
    public void init() {
        Security.addProvider(new BouncyCastleProvider());

        try {
            this.pushService = new PushService(
                    vapidProperties.getPublicKey(),
                    vapidProperties.getPrivateKey(),
                    vapidProperties.getSubject()
            );
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to initialize WebPush PushService", e);
        }
    }

    public void sendToMember(Long memberId, String title, String message) {
        List<WebPushSubscription> subs = subscriptionRepository.findAllByMemberId(memberId);

        if (subs.isEmpty()) {
            log.debug("WebPush 구독 정보 없음. memberId={}", memberId);
            return;
        }

        String payload = """
                {
                  "title": "%s",
                  "message": "%s"
                }
                """.formatted(escapeJson(title), escapeJson(message));

        for (WebPushSubscription sub : subs) {
            try {
                Notification notification = new Notification(
                        sub.getEndpoint(),
                        sub.getP256dh(),
                        sub.getAuth(),
                        payload.getBytes(StandardCharsets.UTF_8)
                );

                HttpResponse response = pushService.send(notification);
                int statusCode = response.getStatusLine().getStatusCode();
                log.info("WebPush 전송 완료. memberId={}, status={}, endpoint={}",
                        memberId, statusCode, sub.getEndpoint());

                if (statusCode == 404 || statusCode == 410) {
                    log.info("유효하지 않은 WebPush 구독 제거. memberId={}, endpoint={}",
                            memberId, sub.getEndpoint());
                    subscriptionRepository.delete(sub);
                }
            } catch (Exception e) {
                log.warn("WebPush 전송 실패. memberId={}, endpoint={}, msg={}",
                        memberId, sub.getEndpoint(), e.getMessage());
            }
        }
    }

    private String escapeJson(String src) {
        if (src == null) return "";
        return src
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}