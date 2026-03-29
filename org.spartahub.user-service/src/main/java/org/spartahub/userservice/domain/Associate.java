package org.spartahub.userservice.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;
import org.spartahub.userservice.domain.exception.InvalidAssociateException;
import org.spartahub.userservice.domain.service.HubInfo;
import org.spartahub.userservice.domain.service.StoreInfo;

import java.util.List;
import java.util.UUID;

import static org.spartahub.userservice.domain.UserType.*;

/**
 * 1. 본사 직원(MASTER, HUB_MANAGER, HUB_DELIVERY)은 특정 거점에 귀속되지 않음.
 * 2. 업체 배송 담당자(STORE_DELIVERY)는 배차를 받는 최종 도착 허브 정보가 필수임.
 * 3. 업체 담당자(STORE_MANAGER)는 소속 업체와 그 업체가 소속된 허브 정보가 모두 필수임.
 */
@Embeddable
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Associate {
    @Embedded
    private Hub hub;

    @Embedded
    private Store store;

    @Builder
    protected Associate(UserType type, UUID hubId, HubInfo hubInfo, UUID storeId, StoreInfo storeInfo) {
        // 본사 직원은 조기에 종료 (필드는 null 유지)
        if (isHeadBranchType(type)) {
            return;
        }

        // 허브 정보가 필요한 역할군 검증 및 생성
        if (type == UserType.STORE_DELIVERY || type == UserType.STORE_MANAGER) {
            if (hubId == null) {
                throw new InvalidAssociateException(type.getDescription() + "는 담당 허브 ID가 필수입니다.");
            }

            this.hub = new Hub(hubId, hubInfo);
        }

        // 업체 정보가 필요한 경우 (STORE_MANAGER)
        if (type == UserType.STORE_MANAGER) {
            if (storeId == null) {
                throw new InvalidAssociateException(type.getDescription() + "는 업체 ID가 필수입니다.");
            }
            this.store = new Store(storeId, storeInfo);
        }
    }

    // 마스터 관리자, 허브 관리자, 허브 배송 담당자는 모두 본사 직원이며 특정 허브에 소속되어 있지 않음
    private boolean isHeadBranchType(UserType type) {
        return List.of(MASTER, HUB_MANAGER, HUB_DELIVERY).contains(type);
    }
}
