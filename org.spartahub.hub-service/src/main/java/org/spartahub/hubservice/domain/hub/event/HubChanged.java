package org.spartahub.hubservice.domain.hub.event;

import org.spartahub.hubservice.domain.hub.HubId;

public record HubChanged(
        HubId hubId
) {}
