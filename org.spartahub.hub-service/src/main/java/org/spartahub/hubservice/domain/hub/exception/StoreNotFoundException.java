package org.spartahub.hubservice.domain.hub.exception;

import org.spartahub.common.exception.NotFoundException;

import java.util.UUID;

public class StoreNotFoundException extends NotFoundException {
    public StoreNotFoundException(UUID id) {
        super("업체를 찾을수 없습니다. ID: " + id);
    }
}
