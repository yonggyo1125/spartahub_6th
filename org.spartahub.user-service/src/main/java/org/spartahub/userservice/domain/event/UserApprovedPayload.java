package org.spartahub.userservice.domain.event;

import org.spartahub.userservice.domain.User;

import java.util.UUID;

public record UserApprovedPayload(
        UUID userId,
        String name, // 관리자명
        String type,
        int deliveryRotationOrder,
        String email,
        String slackId,
        String approvedBy
) {
    public static UserApprovedPayload from(User user) {
        return new UserApprovedPayload(
                user.getId().id(),
                user.getName(),
                user.getType().name(),
                user.getDeliveryRotationOrder(),
                user.getContact().getEmail(),
                user.getContact().getSlackId(),
                user.getApprovedBy()
        );
    }
}
