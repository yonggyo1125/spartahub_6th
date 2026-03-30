package org.spartahub.userservice.infrastructure.kafka;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="topics.user")
public record UserTopicProperties(
    String approved,
    String deleted
) {}
