package org.spartahub.hubservice.infrastructure.provider;

import lombok.RequiredArgsConstructor;
import org.spartahub.common.response.CommonResponse;
import org.spartahub.hubservice.domain.hub.exception.ItemNotFoundException;
import org.spartahub.hubservice.domain.hub.query.HubQueryRepository;
import org.spartahub.hubservice.domain.hub.service.ItemProvider;
import org.spartahub.hubservice.domain.hub.service.dto.ItemData;
import org.spartahub.hubservice.infrastructure.client.ProductClient;
import org.spartahub.hubservice.infrastructure.client.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemProviderImpl implements ItemProvider {

    private final ProductClient client;
    private final HubQueryRepository queryRepository;

    @Override
    public ItemData getItem(UUID storeId, String code) {
        CommonResponse<ProductResponse> response = client.getProduct(storeId, code);

        if (response == null || response.data() == null) {
            throw new ItemNotFoundException(storeId, code);
        }

        ProductResponse product = response.data();

        return new ItemData(product.code(), product.name(), product.memo());
    }

    @Override
    public boolean isDuplicated(UUID storeId, String code) {
        return queryRepository.isDuplicatedItem(storeId, code);
    }
}
