package org.spartahub.userservice.infrastructure.client.dto;

import java.util.UUID;

public record HubResponse(
        UUID id,
        String name,
        String address
) {}
