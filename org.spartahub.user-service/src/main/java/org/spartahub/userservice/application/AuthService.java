package org.spartahub.userservice.application;

import org.spartahub.userservice.presentation.dto.TokenResponse;

public interface AuthService {
    TokenResponse getToken(String email, String password);
    TokenResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
}
