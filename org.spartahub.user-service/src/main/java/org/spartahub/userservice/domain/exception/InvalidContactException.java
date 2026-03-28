package org.spartahub.userservice.domain.exception;

import org.spartahub.common.exception.BadRequestException;

public class InvalidContactException extends BadRequestException {
    public InvalidContactException(String message) {
        super(message);
    }
}
