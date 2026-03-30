package org.spartahub.userservice.domain.exception;

import org.spartahub.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("사용자를 찾을 수 없습니다.");
    }
}
