package org.spartahub.hubservice.application.route;

import org.spartahub.hubservice.domain.hub.HubId;

public interface HubRouteService {
    void recalculateRoutes(HubId hubId);
}
