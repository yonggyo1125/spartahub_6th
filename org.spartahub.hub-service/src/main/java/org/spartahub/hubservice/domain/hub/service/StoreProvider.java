package org.spartahub.hubservice.domain.hub.service;

import org.spartahub.hubservice.domain.hub.service.dto.StoreData;

import java.util.UUID;

public interface StoreProvider {
    StoreData getStore(UUID id);
}
