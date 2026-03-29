package org.spartahub.userservice.infrastructure.client;

import jakarta.ws.rs.InternalServerErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HubClientFallbackFactory implements FallbackFactory<HubClient> {
    @Override
    public HubClient create(Throwable e) {
        return hubId -> {
            log.error("[Hub Service Fallback] ID: {} 조회 중 장애 발생. 사유: {}", hubId, e.getMessage(), e);

            throw new InternalServerErrorException("Hub Service API 요청 처리 실패, 잠시 후 다시 시도해주세요.");
        };
    }
}
