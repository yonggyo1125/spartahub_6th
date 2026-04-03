package org.spartahub.hubservice.infrastructure.client;


import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StoreClientFallbackFactory implements FallbackFactory<StoreClient> {
    @Override
    public StoreClient create(Throwable e) {
        return storeId -> {
            log.error("Store Service Fallback] ID: {} 조회 중 장애 발생. 사유: {}", storeId, e.getMessage(), e);
            throw new InternalServerErrorException("Store Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.");
        };
    }
}