package org.spartahub.userservice.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StoreResponse(
        UUID id,
        String name,
        String address
) {}
