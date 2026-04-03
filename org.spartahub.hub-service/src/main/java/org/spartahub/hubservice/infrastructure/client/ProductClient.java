package org.spartahub.hubservice.infrastructure.client;

import org.spartahub.common.response.CommonResponse;
import org.spartahub.hubservice.infrastructure.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "product-service"
)
public interface ProductClient {
    @GetMapping("/{storeId}/{itemCode}/details")
    CommonResponse<ProductResponse> getProduct(@PathVariable("storeId") UUID storeId, @PathVariable("itemCode") String code);
}
