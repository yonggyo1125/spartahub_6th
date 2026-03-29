package org.spartahub.userservice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.service.StoreData;
import org.spartahub.userservice.domain.service.StoreInfo;
import org.spartahub.userservice.infrastructure.client.StoreClient;
import org.spartahub.userservice.infrastructure.client.dto.StoreResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreInfoImpl implements StoreInfo {

    private final StoreClient client;

    @Override
    public StoreData get(UUID storeId) {
        StoreResponse res = client.getStore(storeId);
        return res == null || res.id() == null ? null : new StoreData(res.name(), res.address());
    }
}
