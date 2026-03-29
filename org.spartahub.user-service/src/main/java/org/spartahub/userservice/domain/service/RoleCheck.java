package org.spartahub.userservice.domain.service;

import org.spartahub.userservice.domain.UserType;

import java.util.List;

public interface RoleCheck {
    boolean hasRole(UserType type);
    boolean hasRole(List<UserType> types);
}
