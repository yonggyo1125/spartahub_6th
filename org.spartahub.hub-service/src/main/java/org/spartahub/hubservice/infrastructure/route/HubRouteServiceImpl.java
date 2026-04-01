package org.spartahub.hubservice.infrastructure.route;

import lombok.RequiredArgsConstructor;
import org.spartahub.hubservice.application.route.HubRouteService;
import org.spartahub.hubservice.domain.hub.HubId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HubRouteServiceImpl implements HubRouteService {

    @Override
    @Transactional
    public void recalculateRoutes(HubId hubId) {

    }
}
