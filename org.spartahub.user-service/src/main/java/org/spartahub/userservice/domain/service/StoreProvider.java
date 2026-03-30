package org.spartahub.userservice.domain.service;

import java.util.UUID;

public interface StoreProvider {
    StoreData get(UUID storeId);
}
