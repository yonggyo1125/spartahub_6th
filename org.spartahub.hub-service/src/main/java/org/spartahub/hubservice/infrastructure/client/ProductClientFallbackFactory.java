package org.spartahub.hubservice.infrastructure.client;

import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable e) {
        return (storeId, code) -> {
            log.error("Product Service Fallback] STORE ID: {}, CODE: {} 조회 중 장애 발생. 사유: {}", storeId, code, e.getMessage(), e);
            throw new InternalServerErrorException("Product Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.");
        };
    }
}
