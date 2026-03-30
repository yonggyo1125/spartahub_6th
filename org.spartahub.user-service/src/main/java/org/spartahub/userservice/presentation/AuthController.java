package org.spartahub.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.userservice.application.AuthService;
import org.spartahub.userservice.presentation.dto.RefreshTokenRequest;
import org.spartahub.userservice.presentation.dto.TokenRequest;
import org.spartahub.userservice.presentation.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "인증 토큰 발급 및 갱신, 로그아웃 API")
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "인증 토큰 발급",
            description = "이메일과 비밀번호를 사용하여 인증을 수행하고, Access/Refresh 토큰을 발급받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 성공",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (아이디/비밀번호 불일치)",
                    content = @Content),
            @ApiResponse(responseCode = "502", description = "인증 서버(Keycloak) 통신 장애",
                    content = @Content)
    })
    @PostMapping("/token")
    public TokenResponse token(@RequestBody @Valid TokenRequest request) {
        log.info("인증 토큰 발급 요청: {}", request.email());
        return authService.getToken(request.email(), request.password());
    }

    @Operation(
            summary = "인증 토큰 갱신",
            description = "만료된 Access 토큰을 대신하여 Refresh 토큰으로 새로운 토큰 세트를 발급받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "갱신 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 유효하지 않음 (재로그인 필요)")
    })
    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.refreshToken(request.refreshToken());
    }

    @Operation(
            summary = "로그아웃",
            description = "리프레시 토큰을 무효화하여 인증 서버의 세션을 종료합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "500", description = "로그아웃 처리 실패")
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody @Valid RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
    }
}
