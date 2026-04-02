package org.spartahub.hubservice.infrastructure.provider;

import lombok.RequiredArgsConstructor;
import org.spartahub.hubservice.domain.hub.service.ItemProvider;
import org.spartahub.hubservice.domain.hub.service.dto.ItemData;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemProviderImpl implements ItemProvider {

    @Override
    public ItemData getItem(UUID storeId, String code) {
        return null;
    }

    @Override
    public boolean isDuplicated(UUID storeId, String code) {
        return false;
    }
}
