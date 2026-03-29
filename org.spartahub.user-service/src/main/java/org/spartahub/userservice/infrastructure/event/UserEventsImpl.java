package org.spartahub.userservice.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.spartahub.common.event.Events;
import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.event.UserApprovedPayload;
import org.spartahub.userservice.domain.event.UserDeletePayload;
import org.spartahub.userservice.domain.event.UserEvents;
import org.spartahub.userservice.infrastructure.kafka.KafkaTopicProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaTopicProperties.class)
public class UserEventsImpl implements UserEvents {

    private final KafkaTopicProperties properties;

    @Override
    public void approved(User user) {
        Events.trigger(getTraceId(), "USER", "APPROVED", properties.approved(), UserApprovedPayload.from(user));
    }

    @Override
    public void deleted(User user) {
        Events.trigger(getTraceId(), "USER", "DELETED", properties.deleted(), UserDeletePayload.from(user));
    }

    // MDC에 저장된 traceId를 가져오고 없다면 새 ID 생성
    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return StringUtils.hasText(traceId) ? traceId : UUID.randomUUID().toString();
    }
}
