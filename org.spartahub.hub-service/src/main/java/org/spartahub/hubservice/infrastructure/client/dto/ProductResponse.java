package org.spartahub.hubservice.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductResponse(
        String code,
        String name,
        String memo
) {}
