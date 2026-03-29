package org.spartahub.userservice.domain.event;

import org.spartahub.userservice.domain.User;

public interface UserEvents {
    void approved(User user);
    void deleted(User user);
}
