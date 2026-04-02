package org.spartahub.hubservice.domain.hub.exception;

import org.spartahub.common.exception.ConflictException;

public class ItemDuplicatedException extends ConflictException {
    public ItemDuplicatedException(String itemCode) {
        super("이미 등록된 상품 코드입니다. - Code: " + itemCode);
    }
}
