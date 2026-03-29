package org.spartahub.userservice.domain.service;

import org.spartahub.userservice.domain.Store;

import java.util.UUID;

public interface StoreInfo {
    Store get(UUID storeId);
}
