package org.spartahub.userservice.application;

import lombok.RequiredArgsConstructor;
import org.spartahub.common.exception.UnAuthorizedException;
import org.spartahub.common.util.SecurityUtil;
import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserRepository;
import org.spartahub.userservice.domain.event.UserEvents;
import org.spartahub.userservice.domain.exception.UserNotFoundException;
import org.spartahub.userservice.domain.service.DeliveryRotationGenerator;
import org.spartahub.userservice.domain.service.IdentityProvider;
import org.spartahub.userservice.domain.service.RoleCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAdminService {
    private final UserEvents userEvents;
    private final RoleCheck roleCheck;
    private final UserRepository userRepository;
    private final IdentityProvider identityProvider;
    private final DeliveryRotationGenerator rotationGenerator;

    // 회원 가입 승인
    @Transactional
    public void approve(UUID userId) {
        User user = getUser(userId);

        user.approve(getMasterId(), roleCheck, rotationGenerator, userEvents);
    }

    // 회원 탈퇴 처리
    @Transactional
    public void delete(UUID userId) {
        User user = getUser(userId);
        user.delete(getMasterId(), roleCheck, userEvents, identityProvider);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(UserId.of(userId)).orElseThrow(UserNotFoundException::new);
    }

    private String getMasterId() {
        return SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new UnAuthorizedException("관리자 인증 정보가 유효하지 않습니다."));
    }
}
