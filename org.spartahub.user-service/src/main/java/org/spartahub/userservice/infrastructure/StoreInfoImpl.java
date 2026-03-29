package org.spartahub.userservice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.Store;
import org.spartahub.userservice.domain.service.StoreInfo;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StoreInfoImpl implements StoreInfo {
    @Override
    public Store get(UUID storeId) {
        return null;
    }
}
