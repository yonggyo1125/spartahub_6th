package org.spartahub.userservice.domain.exception;

import org.spartahub.common.exception.BadRequestException;

public class InvalidAssociateException extends BadRequestException {
    public InvalidAssociateException(String message) {
        super(message);
    }
}
