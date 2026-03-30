package org.spartahub.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "발급된 인증 토큰 응답")
public record TokenResponse(
        @Schema(description = "액세스 토큰 (JWT)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "액세스 토큰 만료 시간 (초 단위)", example = "3600")
        long expiresIn,

        @Schema(description = "리프레시 토큰 만료 시간 (초 단위)", example = "86400")
        long refreshExpiresIn,

        @Schema(description = "리프레시 토큰", example = "def456...")
        String refreshToken,

        @Schema(description = "토큰 타입", example = "Bearer")
        String tokenType
) {}