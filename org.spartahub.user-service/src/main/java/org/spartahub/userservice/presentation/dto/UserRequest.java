package org.spartahub.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.spartahub.userservice.application.dto.UserDto;
import org.spartahub.userservice.domain.UserType;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRequest {
    @Data
    @Schema(description = "회원가입 요청 데이터")
    public static class SignUp {

        @Schema(description = "사용자 이름", example = "이용교", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        private String name;

        @Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8~20자)", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.")
        private String password;

        @Schema(description = "사용자 역할 (MASTER, HUB_MANAGER, HUB_DELIVERY, STORE_MANAGER, STORE_DELIVERY)",
                example = "STORE_MANAGER", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "사용자 타입은 필수 항목입니다.")
        private String type;

        @Schema(description = "소속 허브 ID (업체 담당자/배송 담당자 필수)", example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private UUID hubId;

        @Schema(description = "소속 업체 ID (업체 담당자 필수)", example = "660f8400-f29b-51d4-b716-556655440111",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private UUID storeId;

        @Schema(description = "이메일 주소", example = "yonggyo@spartahub.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "이메일은 필수 입력 항목입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        private String email;

        @Schema(description = "슬랙 ID", example = "U12345678", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "슬랙 ID는 알림 수신을 위해 필수입니다.")
        private String slackId;

        // 응용 계층으로 전달하기 위한 변환 메서드
        public UserDto.SignUp toDto() {
            return UserDto.SignUp.builder()
                    .name(name)
                    .password(password)
                    .type(UserType.valueOf(this.type.toUpperCase()))
                    .hubId(hubId)
                    .storeId(storeId)
                    .email(email)
                    .slackId(slackId)
                    .build();
        }
    }

    @Data
    @Schema(description = "사용자 이름 변경 요청")
    public static class ChangeName {
        @NotBlank(message = "변경할 이름은 필수입니다.")
        @Schema(description = "변경할 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;
    }

    @Data
    @Schema(description = "사용자 연락처 변경 요청")
    public static class ChangeContact {
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수입니다.")
        @Schema(description = "변경할 이메일", example = "new@spartahub.com", requiredMode = Schema.RequiredMode.REQUIRED)
        private String email;

        @NotBlank(message = "슬랙 ID는 필수입니다.")
        @Schema(description = "변경할 슬랙 ID", example = "U98765432", requiredMode = Schema.RequiredMode.REQUIRED)
        private String slackId;
    }

    @Data
    @Schema(description = "사용자 타입 및 소속 변경 요청")
    public static class ChangeAssociate {
        @NotBlank(message = "사용자 타입은 필수입니다.")
        @Schema(description = "변경할 사용자 역할 (MASTER, HUB_MANAGER 등)",
                example = "HUB_DELIVERY", requiredMode = Schema.RequiredMode.REQUIRED)
        private String type; // 도메인 Enum 대신 String으로 받음

        @Schema(description = "변경할 소속 허브 ID", example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private UUID hubId;

        @Schema(description = "변경할 소속 업체 ID", example = "660f8400-f29b-51d4-b716-556655440111",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        private UUID storeId;
    }

    @Data
    @Schema(description = "사용자 비밀번호 변경 요청")
    public static class ChangePassword {
        @Schema(description = "새 비밀번호 (영문, 숫자, 특수문자 포함 8~20자)", example = "NewPass123!", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "새 비밀번호는 필수 입력 항목입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.")
        private String password;
    }
}