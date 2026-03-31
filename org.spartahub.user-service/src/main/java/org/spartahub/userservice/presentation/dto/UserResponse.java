package org.spartahub.userservice.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserType;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    @AllArgsConstructor
    public static class SignUp {
        @Schema(description = "생성된 사용자 ID")
        public UUID id;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "사용자 상세 정보 응답")
    public static class Info {
        @Schema(description = "사용자 ID")
        private UUID id;
        @Schema(description = "사용자 이름")
        private String name;
        @Schema(description = "사용자 역할")
        private UserType type;
        @Schema(description = "이메일")
        private String email;
        @Schema(description = "슬랙 ID")
        private String slackId;
        @Schema(description = "소속 허브 ID")
        private UUID hubId;
        @Schema(description = "소속 허브명")
        private String hubName;
        @Schema(description = "소속 업체 ID")
        private UUID storeId;
        @Schema(description = "소속 업체명")
        private String storeName;
        @Schema(description = "배송 순번")
        private Integer deliveryRotationOrder;
        private boolean enabled;
        public static Info from(User user) {
            return Info.builder()
                    .id(user.getId().id())
                    .name(user.getName())
                    .type(user.getType())
                    .email(user.getContact().getEmail())
                    .slackId(user.getContact().getSlackId())
                    .hubId(user.getAssociate().getHub().getId())
                    .hubName(user.getAssociate().getHub().getName())
                    .storeId(user.getAssociate().getStore().getId())
                    .storeName(user.getAssociate().getStore().getName())
                    .deliveryRotationOrder(user.getDeliveryRotationOrder())
                    .enabled(user.isEnabled())
                    .build();
        }
    }
}
