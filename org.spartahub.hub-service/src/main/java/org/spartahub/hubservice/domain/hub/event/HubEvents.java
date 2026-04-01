package org.spartahub.hubservice.domain.hub.event;

import org.spartahub.hubservice.domain.hub.HubId;

public interface HubEvents {
    void hubChanged(HubId hubId);
}
