package org.spartahub.userservice.domain.service;

import java.util.UUID;

public interface StoreInfo {
    StoreData get(UUID storeId);
}
