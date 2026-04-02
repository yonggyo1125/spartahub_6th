package org.spartahub.hubservice.domain.hub.exception;

import org.spartahub.common.exception.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(String itemCode) {
        super("상품을 찾을 수 없습니다. - Code: " + itemCode);
    }
}
