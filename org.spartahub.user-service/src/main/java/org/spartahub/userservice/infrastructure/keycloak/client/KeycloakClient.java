package org.spartahub.userservice.infrastructure.keycloak.client;

import org.spartahub.userservice.presentation.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "keycloak-client",
        url = "${keycloak.server-url}",
        fallbackFactory = KeycloakClientFallbackFactory.class
)
public interface KeycloakClient {
    @PostMapping(path = "/realms/${keycloak.realm}/protocol/openid-connect/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenResponse getToken(@RequestParam Map<String, String> params);

    @PostMapping(path = "/realms/${keycloak.realm}/protocol/openid-connect/logout",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    void logout(Map<String, String> params);
}
