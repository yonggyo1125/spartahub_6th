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
import org.spartahub.common.response.ErrorResponse;
import org.spartahub.userservice.application.UserSignUpService;
import org.spartahub.userservice.presentation.dto.UserRequest;
import org.spartahub.userservice.presentation.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 계정 생성 및 관리 API")
public class UserController {

    private final UserSignUpService signUpService;

    @Operation(
            summary = "신규 회원가입",
            description = "사용자 정보를 입력받아 시스템 유저를 등록하고 외부 인증 서버(Keycloak)에 계정을 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.SignUp.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터 (Validation 실패)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "이미 등록된 이메일 주소",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "인증 서버(Keycloak) 통신 장애",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse.SignUp signUp(@RequestBody @Valid UserRequest.SignUp request) {
        log.info("회원가입 요청 수신: email={}", request.getEmail());

        UUID userId = signUpService.signUp(request.toDto());

        return new UserResponse.SignUp(userId);
    }
}