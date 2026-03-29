package org.spartahub.userservice.domain.event;

import org.spartahub.userservice.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDeletePayload(
        UUID userId,
        String name,
        String type,
        Integer deliveryRotationOrder, // 퇴사시 배송 순번(허브 배송 담당자, 다른 담당자라면 null),
        LocalDateTime deletedAt,
        String deletedBy
) {
    public static UserDeletePayload from(User user) {
        return new UserDeletePayload(
                user.getId().id(),
                user.getName(),
                user.getType().name(),
                user.getDeliveryRotationOrder(),
                user.getDeletedAt(),
                user.getDeletedBy()
        );
    }
}
