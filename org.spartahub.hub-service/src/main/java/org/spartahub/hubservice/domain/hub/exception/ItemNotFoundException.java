package org.spartahub.hubservice.domain.hub.exception;

import org.spartahub.common.exception.NotFoundException;

import java.util.UUID;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(UUID storeId, String itemCode) {
        super("상품을 찾을 수 없습니다. - Store ID: %s, Code: %s".formatted(storeId, itemCode));
    }
}
