package org.spartahub.hubservice.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.spartahub.hubservice.domain.hub.HubId;
import org.spartahub.hubservice.domain.hub.event.HubChanged;
import org.spartahub.hubservice.domain.hub.event.HubEvents;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubEventsImpl implements HubEvents {
    private final ApplicationEventPublisher publisher;

    @Override
    public void hubChanged(HubId hubId) {
        publisher.publishEvent(new HubChanged(hubId));
    }
}
