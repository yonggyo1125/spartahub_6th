package org.spartahub.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.userservice.application.UserAdminService;
import org.spartahub.userservice.application.UserService;
import org.spartahub.userservice.domain.UserType;
import org.spartahub.userservice.presentation.dto.UserRequest;
import org.spartahub.userservice.presentation.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 계정 생성 및 관리 API")
public class UserController {

    private final UserService userService;
    private final UserAdminService userAdminService;

    @Operation(
            summary = "신규 회원가입",
            description = "사용자 정보를 입력받아 시스템 사용자를 등록하고 외부 인증 서버(Keycloak)에 계정을 생성합니다."
    )
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse.SignUp signUp(@RequestBody @Valid UserRequest.SignUp request) {
        UUID userId = userService.signUp(request.toDto());

        return new UserResponse.SignUp(userId);
    }

    @Operation(summary = "회원 가입 승인", description = "신규 가입 사용자를 승인합니다. 배송 담당자의 경우 배송 순번이 자동 생성됩니다.")
    @PatchMapping("/{userId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(
            @Parameter(description = "승인할 사용자 ID", required = true) @PathVariable UUID userId) {
        userAdminService.approve(userId);
    }

    @Operation(summary = "사용자 이름 변경", description = "MASTER 관리자가 특정 사용자의 이름을 강제로 변경합니다.") // '마스터' -> 'MASTER'
    @PatchMapping("/{userId}/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeName(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable UUID userId,
            @RequestBody @Valid UserRequest.ChangeName request) {
        userAdminService.changeName(userId, request.getName());
    }

    @Operation(summary = "사용자 연락처 변경", description = "사용자의 이메일 및 슬랙 ID를 갱신합니다. 관리자 등 알림 필수 대상은 유효성 검사가 수행됩니다.")
    @PatchMapping("/{userId}/contact")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeContact(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable UUID userId,
            @RequestBody @Valid UserRequest.ChangeContact request) {
        userAdminService.changeContact(userId, request.getEmail(), request.getSlackId());
    }

    @Operation(summary = "사용자 소속 및 역할 변경", description = "사용자 역할(String)과 소속 정보를 변경합니다.")
    @PatchMapping("/{userId}/associate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeAssociate(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable UUID userId,
            @RequestBody @Valid UserRequest.ChangeAssociate request) {

        UserType domainType = UserType.valueOf(request.getType().toUpperCase());

        userAdminService.changeAssociate(userId, domainType, request.getHubId(), request.getStoreId());
    }

    @Operation(summary = "사용자 탈퇴 처리", description = "사용자를 퇴사 처리하고 인증 서버 계정을 무효화합니다.")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "탈퇴 처리할 사용자 ID", required = true) @PathVariable UUID userId) {
        userAdminService.delete(userId);
    }

    @Operation(
            summary = "사용자 비밀번호 변경",
            description = "MASTER 관리자 또는 본인에 한해 사용자 비밀번호를 변경합니다. 외부 인증 서버와 동기화됩니다."
    )
    @PatchMapping("/{userId}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable UUID userId,
            @RequestBody @Valid UserRequest.ChangePassword request) {

        userService.changePassword(userId, request.getPassword());
    }
}