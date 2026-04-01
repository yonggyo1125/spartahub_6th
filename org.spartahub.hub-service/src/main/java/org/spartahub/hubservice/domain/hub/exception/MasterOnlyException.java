package org.spartahub.hubservice.domain.hub.exception;

import org.spartahub.common.exception.ForbiddenException;
import org.spartahub.hubservice.domain.hub.UserType;

public class MasterOnlyException extends ForbiddenException {
    public MasterOnlyException() {
        super(UserType.MASTER.getDescription() + "만 수행할 수 있는 작업입니다.");
    }
}
