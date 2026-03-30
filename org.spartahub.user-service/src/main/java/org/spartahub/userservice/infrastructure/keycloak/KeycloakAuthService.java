package org.spartahub.userservice.infrastructure.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.common.exception.InternalServerException;
import org.spartahub.common.exception.UnAuthorizedException;
import org.spartahub.userservice.application.AuthService;
import org.spartahub.userservice.infrastructure.keycloak.client.KeycloakClient;
import org.spartahub.userservice.infrastructure.keycloak.config.KeycloakProperties;
import org.spartahub.userservice.presentation.dto.TokenResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthService implements AuthService {

    private final KeycloakClient keycloakClient;
    private final KeycloakProperties properties;

    @Override
    public TokenResponse getToken(String email, String password) {
        try {
            Map<String, String> params = Map.of(
                    "grant_type", "password",
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "username", email,
                    "password", password,
                    "scope", "openid"
            );

            log.info("Keycloak 인증 시도 (Email: {})", email);
            return keycloakClient.getToken(params);
        } catch (Exception e) {
            log.error("인증 처리 중 오류 발생 (Email: {}): {}", email, e.getMessage());
            throw new UnAuthorizedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        try {
            Map<String, String> params = Map.of(
                    "grant_type", "refresh_token",
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "refresh_token", refreshToken
            );

            log.info("Keycloak 토큰 갱신 시도");
            return keycloakClient.getToken(params);

        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage(), e);
            throw new UnAuthorizedException("세션이 만료되었습니다. 다시 로그인해주세요.");
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            Map<String, String> params = Map.of(
                    "client_id", properties.clientId(),
                    "client_secret", properties.clientSecret(),
                    "refresh_token", refreshToken
            );

            log.info("Keycloak 로그아웃 시도 (세션 만료)");
            keycloakClient.logout(params);
        } catch (Exception e) {
            log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage(), e);
            throw new InternalServerException("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }
}
