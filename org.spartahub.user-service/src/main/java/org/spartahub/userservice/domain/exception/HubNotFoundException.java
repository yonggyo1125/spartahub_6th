package org.spartahub.userservice.domain.exception;

import org.spartahub.common.exception.NotFoundException;

import java.util.UUID;

public class HubNotFoundException extends NotFoundException {
    public HubNotFoundException(UUID hubId) {
        super("등록되지 않은 허브 정보입니다. HUB ID: " + hubId.toString());
    }
}
