package org.spartahub.hubservice.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.spartahub.hubservice.domain.hub.UserType;
import org.spartahub.hubservice.domain.hub.service.RoleCheck;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityRoleCheck implements RoleCheck {
    @Override
    public boolean hasRole(UserType type) {
        return false;
    }

    @Override
    public boolean hasRole(List<UserType> types) {
        return false;
    }
}
