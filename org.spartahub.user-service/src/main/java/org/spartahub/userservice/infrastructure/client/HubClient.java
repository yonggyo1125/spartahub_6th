package org.spartahub.userservice.infrastructure.client;

import org.spartahub.userservice.infrastructure.client.dto.HubResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name="hub-service",
        fallbackFactory = HubClientFallbackFactory.class
)
public interface HubClient {
    @GetMapping("/{hubId}/details")
    HubResponse getHub(@PathVariable("hubId") UUID hubId);
}