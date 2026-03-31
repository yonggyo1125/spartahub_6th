package org.spartahub.userservice.application;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.common.exception.UnAuthorizedException;
import org.spartahub.common.util.SecurityUtil;
import org.spartahub.userservice.domain.*;
import org.spartahub.userservice.domain.event.UserEvents;
import org.spartahub.userservice.domain.exception.UserNotFoundException;
import org.spartahub.userservice.domain.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserAdminService {
    private final UserEvents userEvents;
    private final RoleCheck roleCheck;
    private final UserRepository userRepository;
    private final UserBulkRepository userBulkRepository;
    private final IdentityProvider identityProvider;
    private final DeliveryRotationGenerator rotationGenerator;
    private final HubProvider hubInfo;
    private final StoreProvider storeProvider;
    private final EntityManager em;

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

    // 허브 정보 일괄 변경
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncHubInfo(Hub hub) {
        long updatedCount = userBulkRepository.bulkUpdateHubInfo(hub);
        em.clear();

        log.info("[BulkUpdate] Hub ID: {} - {} users updated", hub.getId(), updatedCount);
    }

    // 업체 정보 일괄 변경
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncStoreInfo(Store store) {
        long updatedCount = userBulkRepository.bulkUpdateStoreInfo(store);
        em.clear();

        log.info("[BulkUpdate] Store ID: {} - {} users updated", store.getId(), updatedCount);
    }

    private User getUser(UUID userId) {
        return userRepository.findById(UserId.of(userId)).orElseThrow(UserNotFoundException::new);
    }

    private String getMasterId() {
        return SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new UnAuthorizedException("관리자 인증 정보가 유효하지 않습니다."));
    }

}
