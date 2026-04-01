package org.spartahub.hubservice.application.route.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.hubservice.application.route.HubRouteService;
import org.spartahub.hubservice.domain.hub.event.HubChanged;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubRouteEventListener {

    private final HubRouteService routeService;

    @Async
    @Retryable
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHubChanged(HubChanged event) {

        log.info("허브[{}] 변경 감지 -> 경로 재산출", event.hubId());

        routeService.recalculateRoutes(event.hubId());
    }
}
