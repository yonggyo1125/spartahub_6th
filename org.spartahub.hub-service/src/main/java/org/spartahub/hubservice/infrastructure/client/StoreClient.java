package org.spartahub.hubservice.infrastructure.client;

import org.spartahub.common.response.CommonResponse;
import org.spartahub.hubservice.infrastructure.client.dto.StoreResponse;
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
    CommonResponse<StoreResponse> getStore(@PathVariable("storeId") UUID id);
}
