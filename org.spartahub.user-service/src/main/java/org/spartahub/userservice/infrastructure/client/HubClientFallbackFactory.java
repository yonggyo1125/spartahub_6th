package org.spartahub.userservice.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HubClientFallbackFactory implements FallbackFactory<HubClient> {
    @Override
    public HubClient create(Throwable cause) {
        return null;
    }
}
