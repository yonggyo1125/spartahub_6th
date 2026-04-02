package org.spartahub.hubservice.domain.hub.event;

import org.spartahub.hubservice.domain.hub.Hub;

public interface HubEvents {
    void hubChanged(Hub hub, boolean reRoute);
}
