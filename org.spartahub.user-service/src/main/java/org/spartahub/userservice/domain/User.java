package org.spartahub.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.spartahub.common.domain.BaseUserEntity;
import org.spartahub.userservice.domain.exception.InvalidAssociateException;

import java.util.List;
import java.util.UUID;

import static org.spartahub.userservice.domain.UserType.*;


/**
 * 1. 회원 구성은 허브 관리자, 허브 배송 담당자, 업체 배송 담당자, 업체(공급 업체, 생산업체)로 구성 된다. (업체 구분은 업체 도메인에서 정의 한다.)
 * 2. 허브 배송 담당자는 허브간의 이동을 담당하고 스파르타 물류에 10명 존재(그러나 인원수를 제한 하지는 않을 것)
 * 3. 업체 배송 담당자는 최종 허브에서 수령 업체까지의 이동을 담당, 각 허브당 10명이 존재하나 제한하지는 않음, 특정 허브에 소속되어야 하므로 허브 ID를 가지고 있음
 * 4. 사용자 엔티티는 모든 사용자 정보를 관리하며, 사용자 비활성화 시 deleted_by, deleted_at 필드를 통해 관리됩니다.
 * 5. 마스터 관리자 : 모든 기능에 대한 권한이 있는 관리자 입니다.
 * 6. 허브 관리자
 *      - 담당하는 허브를 관리합니다.
 *      - 허브에 속한 배송 담당자를 관리할 수 있습니다.
 *      - 허브에 속한 업체를 관리할 수 있습니다.
 *  7. 배송 담당자
 *      - 허브 배송 담당자: 허브 간 배송을 담당합니다. 허브 → 업체 배송은 불가능 합니다.
 * 업체 배송 담당자: 허브 → 업체 배송을 담당합니다. 허브 간 배송은 불가능 합니다.
 *      - 허브 배송 담당자: 허브 간 배송을 담당합니다. 허브 → 업체 배송은 불가능 합니다.
 * 업체 배송 담당자: 허브 → 업체 배송을 담당합니다. 허브 간 배송은 불가능 합니다.
 * 8. 업체 담당자
 *  - 소속된 업체 정보를 관리합니다. 업체가 등록한 상품을 관리할 수 있습니다. (상품 관리는 업체 도메인에서 정의)
 *  - 소속된 업체 정보를 관리합니다. 업체가 등록한 상품을 관리할 수 있습니다.(상품 관리는 업체 도메인에서 정의)
 *
 */

@Getter
@Entity @ToString
@Table(name="P_USER")
@Access(AccessType.FIELD)
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUserEntity {
    @EmbeddedId
    private UserId id;

    @Version
    private int version;

    @Column(length=20, nullable=false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @Embedded
    private Associate associate; // 직원 소속


    @Embedded
    private Contact contact;

    @Builder
    public User(UserId id, UserType type, UUID hubId, String hubName, String hubAddress, UUID storeId, String storeName, String storeAddress, String email, String slackId) {
        this.id = id;
        this.type = type;

        // 직원 소속 지정
        setAssociate(
                new Associate(
                    new Hub(hubId, hubName, hubAddress),
                    new Store(storeId, storeName, storeAddress)
                ));

        // 연락처 지정
        setContact(email, slackId);

    }

    /**
     * 1. 본사 직원(MASTER, HUB_MANAGER, HUB_DELIVERY)은 특정 거점에 귀속되지 않음.
     * 2. 업체 배송 담당자(STORE_DELIVERY)는 배차를 받는 최종 도착 허브 정보가 필수임.
     * 3. 업체 담당자(STORE_MANAGER)는 소속 업체와 그 업체가 소속된 허브 정보가 모두 필수임.
     */
    private void setAssociate(Associate associate) {
        boolean hasHub = associate.hub() != null && associate.hub().id() != null && associate.hub().name() != null &&  associate.hub().address() != null;
        boolean hasStore = associate.store() != null && associate.store().id() != null;

        // 본사 직원은 소속 정보를 저장하지 않음
        if (isHeadBranchType(type)) {
            this.associate = new Associate(null, null);
            return;
        }

        if (type == UserType.STORE_DELIVERY) {
            if (!hasHub) {
                throw new InvalidAssociateException(type.getDescription() + "는 담당 허브 정보가 필수입니다.");
            }

            // 업체 정보는 필요 없으므로 제거
            this.associate = new Associate(associate.hub(), null);
            return;

        } else if (type == UserType.STORE_MANAGER) {
            if (!hasHub || !hasStore) {
                throw new InvalidAssociateException("업체 담당자는 업체 정보와 업체 소속 허브 정보 모두 필수입니다.");
            }
        }

        this.associate = associate;
    }

    // 마스터 관리자, 허브 관리자, 허브 배송 담당자는 모두 본사 직원이며 특정 허브에 소속되어 있지 않음
    private boolean isHeadBranchType(UserType type) {
        return List.of(MASTER, HUB_MANAGER, HUB_DELIVERY).contains(type);
    }

    /**
     * 허브 관리자는 주문이 접수되고 배송에 대한 허브 -> 업체 이동이 결정이 되면 최종 배송지까지의 거리와 운행 스케줄에 따라 최종 배송 시한 메세지를 LLM을 통해 자동 생성하고 슬랙메세지 + 이메일로 전송됩니다.(매일 자정)
     * 업체 배송 관리자는 새벽 6시에 그날 배송할 업체 목록의 배송 동선을 LLM + 도구 연동을 통해 생성된 메세지를 슬랙 + 이메일로 전송됩니다.
     */
    private void setContact(String email, String slackId) {

    }
}
