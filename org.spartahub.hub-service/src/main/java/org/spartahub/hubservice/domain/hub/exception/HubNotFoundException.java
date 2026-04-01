package org.spartahub.hubservice.domain.hub.exception;

import org.spartahub.common.exception.NotFoundException;
import org.spartahub.hubservice.domain.hub.HubId;

import java.util.UUID;

public class HubNotFoundException extends NotFoundException {
    public HubNotFoundException(UUID hubId) {
        super("허브를 찾을 수 없습니다. Hub ID: " + hubId);
    }

    public HubNotFoundException(HubId hubId) {
        this(hubId.id());
    }
}
