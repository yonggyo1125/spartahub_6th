package org.spartahub.hubservice.infrastructure.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="topics.hub")
public record HubTopics(
        String updated
) {}
