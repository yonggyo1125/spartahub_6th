package org.spartahub.userservice.infrastructure.client;

import org.spartahub.userservice.infrastructure.client.dto.StoreResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "store-service",
        fallbackFactory = StoreClientFallbackFactory.class
)
public interface StoreClient {
    @GetMapping("/{storeId}/details")
    StoreResponse getStore(@PathVariable("storeId") UUID id);
}
