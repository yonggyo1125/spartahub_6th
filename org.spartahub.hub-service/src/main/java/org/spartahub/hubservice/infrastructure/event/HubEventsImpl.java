package org.spartahub.hubservice.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.spartahub.common.event.Events;
import org.spartahub.hubservice.domain.hub.Hub;
import org.spartahub.hubservice.domain.hub.event.HubChanged;
import org.spartahub.hubservice.domain.hub.event.HubEvents;
import org.spartahub.hubservice.infrastructure.event.dto.HubMessage;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(HubTopics.class)
public class HubEventsImpl implements HubEvents {

    private final ApplicationEventPublisher publisher;
    private final HubTopics hubTopics;

    @Override
    public void hubChanged(Hub hub, boolean reRoute) {

        // 허브 변경시 허브 경로 재산출을 위한 내부 이벤트 발행
        if (reRoute) {
            publisher.publishEvent(new HubChanged(hub.getId()));
        }
        // 다른 애그리거트(다른 서버)에 있는 허브 정보 업데이트를 위한 카프카 메세지 발행
        Events.trigger(UUID.randomUUID().toString(), "HUB", "HUB_CHANGED", hubTopics.updated(), HubMessage.from(hub));
    }
}
