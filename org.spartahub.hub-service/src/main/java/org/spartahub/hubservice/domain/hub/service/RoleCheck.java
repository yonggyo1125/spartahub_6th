package org.spartahub.hubservice.domain.hub.service;

import org.spartahub.hubservice.domain.hub.UserType;

import java.util.List;

public interface RoleCheck {
    boolean hasRole(UserType type);
    boolean hasRole(List<UserType> types);
}
