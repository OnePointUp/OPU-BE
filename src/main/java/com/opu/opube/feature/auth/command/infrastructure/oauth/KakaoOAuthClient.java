package com.opu.opube.feature.auth.command.infrastructure.oauth;

import com.opu.opube.exception.BusinessException;
import com.opu.opube.exception.ErrorCode;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoTokenResponse;
import com.opu.opube.feature.auth.command.application.dto.response.KakaoUserInfoResponse;
import com.opu.opube.feature.auth.command.config.KakaoOAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient {

    private final WebClient webClient;
    private final KakaoOAuthProperties kakaoProps;

    public KakaoTokenResponse requestToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", kakaoProps.getClientId());
        form.add("redirect_uri", kakaoProps.getRedirectUri());
        form.add("code", code);

        if (StringUtils.hasText(kakaoProps.getClientSecret())) {
            form.add("client_secret", kakaoProps.getClientSecret());
        }

        KakaoTokenResponse tokenResponse = webClient.post()
                .uri(kakaoProps.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new BusinessException(
                                        ErrorCode.OAUTH_LOGIN_FAILED,
                                        "카카오 토큰 발급 실패: " + body
                                ))
                )
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        if (tokenResponse == null || !StringUtils.hasText(tokenResponse.getAccessToken())) {
            throw new BusinessException(ErrorCode.OAUTH_LOGIN_FAILED, "카카오 토큰 발급에 실패했습니다.");
        }

        return tokenResponse;
    }

    public KakaoUserInfoResponse requestUserInfo(String accessToken) {
        KakaoUserInfoResponse userInfo = webClient.get()
                .uri(kakaoProps.getUserInfoUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .map(body -> new BusinessException(
                                        ErrorCode.OAUTH_LOGIN_FAILED,
                                        "카카오 사용자 정보 조회 실패: " + body
                                ))
                )
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

        if (userInfo == null || userInfo.getId() == null) {
            throw new BusinessException(ErrorCode.OAUTH_LOGIN_FAILED, "카카오 사용자 정보 조회에 실패했습니다.");
        }

        return userInfo;
    }

    public void unlink(String providerId) {
        try {
            webClient.post()
                    .uri(kakaoProps.getUnlinkUri())
                    .header(HttpHeaders.AUTHORIZATION, "KakaoAK " + kakaoProps.getAdminKey())
                    .body(BodyInserters
                            .fromFormData("target_id_type", "user_id")
                            .with("target_id", providerId))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("카카오 계정 unlink 성공. providerId={}", providerId);
        } catch (Exception e) {
            log.warn("카카오 계정 unlink 실패. providerId={}", providerId, e);
        }
    }
}

