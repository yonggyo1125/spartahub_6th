package org.spartahub.userservice.application;

import lombok.RequiredArgsConstructor;
import org.spartahub.common.exception.UnAuthorizedException;
import org.spartahub.common.util.SecurityUtil;
import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserRepository;
import org.spartahub.userservice.domain.UserType;
import org.spartahub.userservice.domain.event.UserEvents;
import org.spartahub.userservice.domain.exception.UserNotFoundException;
import org.spartahub.userservice.domain.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {
    private final UserEvents userEvents;
    private final RoleCheck roleCheck;
    private final UserRepository userRepository;
    private final IdentityProvider identityProvider;
    private final DeliveryRotationGenerator rotationGenerator;
    private final HubProvider hubInfo;
    private final StoreProvider storeProvider;

    // 가입 승인
    public void approve(UUID userId) {
        User user = getUser(userId);

        user.approve(getMasterId(), roleCheck, rotationGenerator, userEvents);
    }

    // 탈퇴 처리
    public void delete(UUID userId) {
        User user = getUser(userId);
        user.delete(getMasterId(), roleCheck, userEvents, identityProvider);
    }

    // 소속 변경
    public void changeAssociate(UUID userId, UserType type, UUID hubId, UUID storeId) {
        User user = getUser(userId);
        user.changeAssociate(type, hubId, storeId, roleCheck, hubInfo, storeProvider, rotationGenerator);
    }

    // 연락처 변경
    public void changeContact(UUID userId, String email, String slackId) {
        User user = getUser(userId);
        user.changeContact(email, slackId, roleCheck);
    }

    // 이름 변경
    public void changeName(UUID userId, String name) {
        User user = getUser(userId);
        user.changeName(name, roleCheck);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(UserId.of(userId)).orElseThrow(UserNotFoundException::new);
    }

    private String getMasterId() {
        return SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new UnAuthorizedException("관리자 인증 정보가 유효하지 않습니다."));
    }
}
