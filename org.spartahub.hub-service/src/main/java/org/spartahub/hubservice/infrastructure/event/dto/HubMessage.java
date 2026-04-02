package org.spartahub.hubservice.infrastructure.event.dto;

import org.spartahub.hubservice.domain.hub.Hub;

import java.util.UUID;

public record HubMessage(
        UUID hubId,
        String name,
        String address,
        Double latitude,
        Double longitude,
        boolean isDeleted
) {
    public static HubMessage from(Hub hub) {
        return new HubMessage(
                hub.getId().id(),
                hub.getName(),
                hub.getLocation().getAddress(),
                hub.getLocation().getLatitude(),
                hub.getLocation().getLongitude(),
                hub.getDeletedAt() != null
        );
    }
}
