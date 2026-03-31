package org.spartahub.gatewayserver.response;

import java.util.UUID;

public record User(
        UUID id,
        String name,
        String type,
        String email,
        String slackId
) {}
