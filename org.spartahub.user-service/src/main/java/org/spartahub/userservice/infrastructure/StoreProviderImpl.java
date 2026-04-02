package org.spartahub.userservice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.spartahub.common.response.CommonResponse;
import org.spartahub.userservice.domain.service.StoreData;
import org.spartahub.userservice.domain.service.StoreProvider;
import org.spartahub.userservice.infrastructure.client.StoreClient;
import org.spartahub.userservice.infrastructure.client.dto.StoreResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreProviderImpl implements StoreProvider {

    private final StoreClient client;

    @Override
    public StoreData get(UUID storeId) {
        CommonResponse<StoreResponse> res = client.getStore(storeId);
        return res == null || res.data() == null || res.data().id() == null ? null : new StoreData(res.data().name(), res.data().address());
    }
}
