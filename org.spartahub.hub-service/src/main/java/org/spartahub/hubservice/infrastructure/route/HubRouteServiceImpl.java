package org.spartahub.hubservice.infrastructure.route;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.hubservice.application.route.HubRouteService;
import org.spartahub.hubservice.domain.hub.HubId;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubRouteServiceImpl implements HubRouteService {

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            retryFor = { Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 5000, multiplier = 2)
    )
    public void recalculateRoutes(HubId hubId) {

    }

    @Recover // 모든 재시도가 실패한 경우 처리
    public void recover(Exception e, HubId hubId) {
        log.error("허브 경로 재시도 모두 실패: {}", e.getMessage(), e);
    }
}
