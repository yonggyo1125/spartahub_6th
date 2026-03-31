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
            // 1. Contact 정보 방어 (email, slackId)
            var contact = user.getContact();
            // 2. Associate 정보 방어 (hub, store)
            var associate = user.getAssociate();

            return Info.builder()
                    .id(user.getId() != null ? user.getId().id() : null)
                    .name(user.getName())
                    .type(user.getType())
                    .email(contact != null ? contact.getEmail() : null)
                    .slackId(contact != null ? contact.getSlackId() : null)
                    .hubId(associate != null && associate.getHub() != null ? associate.getHub().getId() : null)
                    .hubName(associate != null && associate.getHub() != null ? associate.getHub().getName() : null)
                    .storeId(associate != null && associate.getStore() != null ? associate.getStore().getId() : null)
                    .storeName(associate != null && associate.getStore() != null ? associate.getStore().getName() : null)

                    .deliveryRotationOrder(user.getDeliveryRotationOrder())
                    .enabled(user.isEnabled())
                    .build();
        }
    }
}
