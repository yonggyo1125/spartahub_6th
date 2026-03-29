package org.spartahub.userservice.domain.exception;

import org.spartahub.common.exception.NotFoundException;

import java.util.UUID;

public class StoreNotFoundException extends NotFoundException {
    public StoreNotFoundException(UUID id) {
        super("등록되지 않은 업체 정보입니다. STORE ID: " + id.toString());
    }
}
