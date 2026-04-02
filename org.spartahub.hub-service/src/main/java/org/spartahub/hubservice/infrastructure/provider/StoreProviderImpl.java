package org.spartahub.hubservice.infrastructure.provider;

import lombok.RequiredArgsConstructor;
import org.spartahub.hubservice.domain.hub.service.StoreProvider;
import org.spartahub.hubservice.domain.hub.service.dto.StoreData;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreProviderImpl implements StoreProvider {
    @Override
    public StoreData getStore(UUID id) {
        return null;
    }
}
