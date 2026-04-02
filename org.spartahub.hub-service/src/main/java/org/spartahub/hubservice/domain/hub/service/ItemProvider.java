package org.spartahub.hubservice.domain.hub.service;

import org.spartahub.hubservice.domain.hub.service.dto.ItemData;

import java.util.UUID;

public interface ItemProvider {
    ItemData getItem(UUID storeId, String code);
}
