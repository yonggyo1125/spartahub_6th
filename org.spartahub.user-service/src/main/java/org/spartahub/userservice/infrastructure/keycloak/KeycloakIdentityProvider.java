package org.spartahub.userservice.infrastructure.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.spartahub.common.exception.ConflictException;
import org.spartahub.userservice.domain.service.IdentityProvider;
import org.spartahub.userservice.domain.exception.IdentityProviderException;
import org.spartahub.userservice.infrastructure.keycloak.config.KeycloakProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakIdentityProvider implements IdentityProvider {

    private final Keycloak keycloak;
    private final KeycloakProperties properties;

    @Override
    public UUID register(String email, String password) {
        // UserRepresentation 설정
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(email.toLowerCase());
        user.setEmail(email);
        user.setEmailVerified(true); // 이메일 인증이 필요한 경우 false로 설정하고, 인증 완료 시 true로 변경한다.

        // 비밀번호 설정
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false); // 임시 비밀번호 발급이 아니므로 false로 설정
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        user.setCredentials(Collections.singletonList(credential));

        // Keycloak에 사용자 생성 요청
        UsersResource usersResource = keycloak.realm(properties.realm()).users();
        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == HttpStatus.CONFLICT.value()) {
                throw new ConflictException("이미 등록된 이메일 주소입니다.");
            }

            if (response.getStatus() != HttpStatus.CREATED.value()) {
                log.error("Keycloak 회원 등록 실패: 상태코드 {}", response.getStatus());

                throw new IdentityProviderException("인증 서버 등록에 실패하였습니다.");
            }

            // 생성된 사용자의 UUID 추출 (Location 헤더에 포함됨)
            if (response.getLocation() == null) {
                log.error("Keycloak 응답에 Location 헤더가 누락되었습니다.");
                throw new IdentityProviderException("인증 서버로부터 식별자를 받아오지 못했습니다.");
            }

            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("Keycloak 회원 등록 성공: {}", userId);

            return UUID.fromString(userId);
        }
    }

    @Override
    public void withdraw(UUID userId) {
        try {
            keycloak.realm(properties.realm()).users().get(userId.toString()).remove();
        } catch (Exception e) {
            // 보상 트랜잭션 실패 시 수동 조치할 수 있도록 로그를 남긴다.
            log.error("Keycloak 회원 삭제 실패 (ID: {}): {}", userId, e.getMessage());
        }
    }
}
