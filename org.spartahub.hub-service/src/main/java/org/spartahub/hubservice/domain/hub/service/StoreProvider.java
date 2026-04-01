package org.spartahub.hubservice.domain.hub.service;

import java.util.UUID;

public interface StoreProvider {
    StoreData getStore(UUID id);
}
