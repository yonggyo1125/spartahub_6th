package org.spartahub.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "인증 토큰 갱신 요청 (Refresh Token)")
public record RefreshTokenRequest(
        @Schema(
                description = "발급받았던 리프레시 토큰",
                example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "리프레시 토큰은 필수 입력 값입니다.")
        String refreshToken
) {}