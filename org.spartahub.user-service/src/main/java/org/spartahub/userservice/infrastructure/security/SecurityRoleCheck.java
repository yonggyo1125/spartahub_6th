package org.spartahub.userservice.infrastructure.security;

import org.spartahub.common.util.SecurityUtil;
import org.spartahub.userservice.domain.UserType;
import org.spartahub.userservice.domain.service.RoleCheck;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityRoleCheck implements RoleCheck {

    @Override
    public boolean hasRole(UserType type) {
        return hasRole(List.of(type));
    }

    @Override
    public boolean hasRole(List<UserType> types) {

        if (types == null || types.isEmpty()) return false;

        // 현재 사용자 정보를 한 번만 획득
        return SecurityUtil.getCurrentUser()
                .map(userDetails -> {
                    String roles = userDetails.getRoles();
                    if (!StringUtils.hasText(roles)) return false;

                    // 권한 목록을 셋(Set)으로 변환 (중복 제거 및 조회 성능 향상)
                    Set<String> userRoleList = Arrays.stream(roles.split(","))
                            .map(String::trim)
                            .collect(Collectors.toSet());

                    if (userRoleList.contains(UserType.MASTER.toRole())) return true;

                    // 전달받은 타입들 중 하나라도 유저 권한 목록에 있는지 확인
                    return types.stream()
                            .map(UserType::toRole)
                            .anyMatch(userRoleList::contains);
                })
                .orElse(false);
    }
}
