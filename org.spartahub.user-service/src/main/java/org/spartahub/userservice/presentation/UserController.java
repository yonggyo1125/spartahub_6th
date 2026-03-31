package org.spartahub.userservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.common.exception.UnAuthorizedException;
import org.spartahub.common.util.SecurityUtil;
import org.spartahub.userservice.application.UserAdminService;
import org.spartahub.userservice.application.UserService;
import org.spartahub.userservice.application.query.UserQueryService;
import org.spartahub.userservice.domain.UserType;
import org.spartahub.userservice.presentation.dto.UserRequest;
import org.spartahub.userservice.presentation.dto.UserResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "사용자 API", description = "사용자 계정 생성 및 관리 API")
public class UserController {

    private final UserService userService;
    private final UserAdminService userAdminService;
    private final UserQueryService userQueryService;

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

    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "회원 가입 승인", description = "신규 가입 사용자를 승인합니다. 배송 담당자의 경우 배송 순번이 자동 생성됩니다.")
    @PatchMapping("/{userId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(
            @Parameter(description = "승인할 사용자 ID", required = true) @PathVariable UUID userId) {
        userAdminService.approve(userId);
    }

    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "사용자 이름 변경", description = "MASTER 관리자가 특정 사용자의 이름을 강제로 변경합니다.") // '마스터' -> 'MASTER'
    @PatchMapping("/{userId}/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeName(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable UUID userId,
            @RequestBody @Valid UserRequest.ChangeName request) {
        userAdminService.changeName(userId, request.getName());
    }

    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "사용자 연락처 변경", description = "사용자의 이메일 및 슬랙 ID를 갱신합니다. 관리자 등 알림 필수 대상은 유효성 검사가 수행됩니다.")
    @PatchMapping("/{userId}/contact")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeContact(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable UUID userId,
            @RequestBody @Valid UserRequest.ChangeContact request) {
        userAdminService.changeContact(userId, request.getEmail(), request.getSlackId());
    }

    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "사용자 소속 및 역할 변경", description = "사용자 역할(String)과 소속 정보를 변경합니다.")
    @PatchMapping("/{userId}/associate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeAssociate(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable UUID userId,
            @RequestBody @Valid UserRequest.ChangeAssociate request) {

        UserType domainType = UserType.valueOf(request.getType().toUpperCase());

        userAdminService.changeAssociate(userId, domainType, request.getHubId(), request.getStoreId());
    }

    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "사용자 탈퇴 처리", description = "사용자를 퇴사 처리하고 인증 서버 계정을 무효화합니다.")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "탈퇴 처리할 사용자 ID", required = true) @PathVariable UUID userId) {
        userAdminService.delete(userId);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'HUB_DELIVERY', 'STORE_DELIVERY', 'STORE_MANAGER')")
    @Operation(
            summary = "사용자 비밀번호 변경",
            description = "MASTER 관리자 또는 본인에 한해 사용자 비밀번호를 변경합니다. 외부 인증 서버와 동기화됩니다."
    )
    @PatchMapping({"/password", "/{userId}/password"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @Parameter(description = "대상 사용자 ID", required = true) @PathVariable(name="userId", required = false) UUID userId,
            @RequestBody @Valid UserRequest.ChangePassword request) {

        userService.changePassword(userId == null ? getUserId() : userId, request.getPassword());
    }

    // 조회 API S
    /**
     * @PageableAsQueryParam: Swagger UI에 page, size, sort 파라미터를 생성함
     * @Parameter(hidden = true): 기본 Pageable 객체의 복잡한 구조가 노출되는 것을 방지함
     * @ParameterObject: Search DTO의 필드들을 평평하게(flat) 펼쳐서 쿼리 파라미터로 인식하게 함
     */
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    @Operation(summary = "사용자 상세 조회", description = "ID를 통해 특정 사용자의 상세 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public UserResponse.Info getUser(@PathVariable UUID userId) {
        return userQueryService.getUser(userId);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    @Operation(summary = "사용자 목록 검색 (MASTER, HUB_MANAGER)", description = "MASTER 관리자용 전체 사용자 통합 검색 기능을 제공합니다.")
    @GetMapping
    @PageableAsQueryParam
    public Page<UserResponse.Info> searchUsers(
            @ParameterObject UserRequest.Search request,
            @Parameter(hidden = true) Pageable pageable) {
        return userQueryService.searchUsers(request.toDomainDto(), pageable);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    @Operation(summary = "허브별 사용자 조회", description = "특정 허브에 소속된 사용자 목록을 조회합니다.")
    @GetMapping("/hubs/{hubId}")
    @PageableAsQueryParam
    public Page<UserResponse.Info> searchUsersByHub(
            @PathVariable UUID hubId,
            @ParameterObject UserRequest.Search request,
            @Parameter(hidden = true) Pageable pageable) {
        return userQueryService.searchUsersByHub(hubId, request.toDomainDto(), pageable);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    @Operation(summary = "업체별 사용자 조회", description = "특정 업체에 소속된 사용자 목록을 조회합니다.")
    @GetMapping("/stores/{storeId}")
    @PageableAsQueryParam
    public Page<UserResponse.Info> searchUsersByStore(
            @PathVariable UUID storeId,
            @ParameterObject UserRequest.Search request,
            @Parameter(hidden = true) Pageable pageable) {
        return userQueryService.searchUsersByStore(storeId, request.toDomainDto(), pageable);
    }

    @Operation(summary = "타입별 사용자 조회", description = "사용자 역할(MASTER, HUB_DELIVERY 등)을 기준으로 목록을 조회합니다.")
    @GetMapping("/types/{type}")
    @PageableAsQueryParam
    public Page<UserResponse.Info> searchUsersByType(
            @PathVariable String type,
            @ParameterObject UserRequest.Search request,
            @Parameter(hidden = true) Pageable pageable) {
        return userQueryService.searchUsersByType(type, request.toDomainDto(), pageable);
    }

    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'HUB_DELIVERY', 'STORE_DELIVERY', 'STORE_MANAGER')")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 상세 정보를 반환합니다.")
    @GetMapping("/me")
    public UserResponse.Info getMyInfo(
            @RequestHeader(name = "X-User-UUID", required = false) UUID gatewayUserId) {

        // 게이트웨이 헤더 우선, 없으면 시큐리티 컨텍스트에서 추출
        UUID targetId = (gatewayUserId != null) ? gatewayUserId : getUserId();

        if (targetId == null) {
            throw new UnAuthorizedException("사용자 식별 정보가 없습니다.");
        }

        return userQueryService.getUser(targetId);
    }
    // 조회 API E

    private UUID getUserId() {
        return SecurityUtil.getCurrentUserId().orElse(null);
    }
}