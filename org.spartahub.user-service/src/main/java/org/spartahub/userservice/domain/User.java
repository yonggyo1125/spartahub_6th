package org.spartahub.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.spartahub.common.domain.BaseUserEntity;
import org.spartahub.common.exception.BadRequestException;
import org.spartahub.common.exception.ForbiddenException;
import org.spartahub.userservice.domain.event.UserEvents;
import org.spartahub.userservice.domain.service.*;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static org.spartahub.userservice.domain.UserType.*;

/**
 * 1. 회원 구성은 허브 관리자, 허브 배송 담당자, 업체 배송 담당자, 업체(공급 업체, 생산업체)로 구성 된다. (업체 구분은 업체 도메인에서 정의 한다.)
 * 2. 허브 배송 담당자는 허브간의 이동을 담당하고 스파르타 물류에 10명 존재(그러나 인원수를 제한 하지는 않을 것)
 * 3. 업체 배송 담당자는 최종 허브에서 수령 업체까지의 이동을 담당, 각 허브당 10명이 존재하나 제한하지는 않음, 특정 허브에 소속되어야 하므로 허브 ID를 가지고 있음
 * 4. 사용자 엔티티는 모든 사용자 정보를 관리하며, 사용자 비활성화 시 deleted_by, deleted_at 필드를 통해 관리됩니다.
 * 5. 마스터 관리자 : 모든 기능에 대한 권한이 있는 관리자 입니다., 배송 순번 역시 마스터 관리자가 승인시 지정
 * 6. 허브 관리자
 *      - 담당하는 허브를 관리합니다.
 *      - 허브에 속한 배송 담당자를 관리할 수 있습니다.
 *      - 허브에 속한 업체를 관리할 수 있습니다.
 *  7. 배송 담당자
 *      - 허브 배송 담당자: 허브 간 배송을 담당합니다. 허브 → 업체 배송은 불가능 합니다.
 *      - 배송 담당자는 배송 순번이 있으며 배송 도메인에서 배송 배정시 이 순번대로 할당 됩니다.
 *      - 배송 순번은 최종 최초 승인이 될때 결정 된다.
 *
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
@Table(
        name = "P_USER",
        indexes = {
                @Index(
                        name = "idx_user_type_rotation_order_desc",
                        columnList = "type, delivery_rotation_order DESC, deleted_at"
                )
        }
)
@Access(AccessType.FIELD)
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUserEntity {

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$";

    @EmbeddedId
    private UserId id;

    @Version
    private int version;

    @Column(length=50)
    private String name; // 사용자명

    @Column(length=20, nullable=false)
    @Enumerated(EnumType.STRING)
    private UserType type;

    @Embedded
    private Associate associate; // 직원 소속

    private Integer deliveryRotationOrder; // 허브 배송 담당자의 배송 순번

    @Embedded
    private Contact contact;

    private boolean approved; // 승인 여부

    private String approvedBy; // 승인 관리자 아이디

    @Builder
    public User(UserId id, String name, UserType type, UUID hubId, HubProvider hubProvider, UUID storeId, StoreProvider storeInfo, String email, String slackId) {
        this.id = id;
        this.name = name;
        this.type = type;

        // 직원 소속 지정
        this.associate = new Associate(type, hubId, hubProvider, storeId, storeInfo);

        // 연락처 지정
        setContact(email, slackId);

        // MASTER 타입 회원의 경우 approved를 true로 고정
        if (this.type == MASTER) {
            this.approved = true;
            this.approvedBy = "SYSTEM";
        }
    }

    // 사용자 이름 변경(MASTER 관리자만 가능)
    public void changeName(String name, RoleCheck roleCheck) {
        checkMaster(roleCheck);
        this.name = name;
    }

    // 사용자 소속 변경(MASTER 관리자만 가능)
    public void changeAssociate(UserType type, UUID hubId, UUID storeId, RoleCheck roleCheck, HubProvider hubInfo, StoreProvider storeProvider, DeliveryRotationGenerator generator) {
        checkMaster(roleCheck);

        // 타입이 변경된 경우만 수행
        if (this.type != type) {
            this.type = type;

            // 허브 배송 담당자로 변경되는 경우라면 순번을 다시 할당해야 하고, 그 외에는 null로 변경이 되어야 함
            if (this.type == HUB_DELIVERY) {
                if (this.deliveryRotationOrder == null) {
                    if (generator == null) {
                        throw new BadRequestException(type.getDescription() + " 승인 시 순번 생성기는 필수입니다.");
                    }

                    this.deliveryRotationOrder = generator.next();
                }
            } else {
                this.deliveryRotationOrder = null;
            }
        }

        hubId = hubId == null ? this.associate.getHub().getId() : hubId;
        storeId = storeId == null ? this.associate.getStore().getId() : storeId;

        this.associate = new Associate(type, hubId, hubInfo, storeId, storeProvider);

        setContact(this.contact.getEmail(), this.contact.getSlackId());
    }

    /**
     * 허브 관리자는 주문이 접수되고 배송에 대한 허브 -> 업체 이동이 결정이 되면 최종 배송지까지의 거리와 운행 스케줄에 따라 최종 배송 시한 메세지를 LLM을 통해 자동 생성하고 슬랙메세지 + 이메일로 전송됩니다.(매일 자정)
     * 업체 배송 관리자는 새벽 6시에 그날 배송할 업체 목록의 배송 동선을 LLM + 도구 연동을 통해 생성된 메세지를 슬랙 + 이메일로 전송됩니다.
     */
    private void setContact(String email, String slackId) {
        // 허브 관리자, 업체 배송 관리자는 알림 메세지가 필수
        boolean isNotificationRequired = type == HUB_MANAGER || type == STORE_DELIVERY;

        // 이메일, 슬랙 ID 필수 여부 체크
        if (isNotificationRequired && (!StringUtils.hasText(email) || !StringUtils.hasText(slackId))) {
            throw new BadRequestException("%s는 알림 수신을 위한 이메일과 슬랙 ID가 필수입니다.".formatted(type.getDescription()));
        }

        this.contact = new Contact(email, slackId);
    }

    // 연락처 변경(MASTER 관리자만 가능)
    public void changeContact(String email, String slackId, RoleCheck roleCheck) {
        checkMaster(roleCheck);
        setContact(email, slackId);
    }

    /**
     * 사용자 비밀번호를 변경합니다.
     * 1. MASTER 관리자 또는 본인(소유자)만 변경 권한을 가집니다.
     * 2. 실제 비밀번호 변경 처리는 외부 인증 서버를 통해 수행됩니다.
     */
    public void changePassword(String password, RoleCheck roleCheck, IdentityProvider identityProvider) {
        // 권한 체크
        if (roleCheck.hasRole(MASTER) && !roleCheck.isMine(this.id)) {
            throw new ForbiddenException("비밀번호를 변경할 권한이 없습니다.");
        }

        // 비밀번호 유효성 검사
        if (!StringUtils.hasText(password) || !password.matches(PASSWORD_REGEX)) {
            throw new BadRequestException("비밀번호는 영문, 숫자, 특수문자를 포함하여 8~20자여야 합니다.");
        }

        // 외부 인증 서버 변경 요청
        identityProvider.changePassword(id, password);
    }

     // MASTER를 제외한 모든 사용자는 승인을 통해야만 권한이 활성화 됩니다.
     // 허브 배송 담당자를 승인 할 때 다음 배송 순번 결정(수정시에는 변경 불가, 최초 한번 등록)
     public void approve(String approver, RoleCheck roleCheck, DeliveryRotationGenerator rotationGenerator, UserEvents userEvents) {

         // MASTER ID 체크
         checkMasterId(approver);

         // 마스터 권한 체크
         checkMaster(roleCheck);

        // 이미 승인된 경우는 처리 하지 않음
        if (this.approved) {
            return;
        }

        this.approved = true;
        this.approvedBy = approver; // 승인한 MASTER 관리자 아이디

         // 허브 배송 담당자의 배송 순번 지정하기
         if (this.type == HUB_DELIVERY) {
             if (rotationGenerator == null) {
                 throw new BadRequestException(type.getDescription() + " 승인 시 순번 생성기는 필수입니다.");
             }

             this.deliveryRotationOrder = rotationGenerator.next();
         }
        // 승인 완료 시 이메일 또는 메세지 전송
         userEvents.approved(this);
     }

     // 직원 퇴사시 soft delete 삭제, MASTER 관리자만 가능
     public void delete(String masterId, RoleCheck roleCheck, UserEvents userEvents, IdentityProvider identityProvider) {
         // 이미 탈퇴한 경우 처리하지 않음
         if (this.deletedAt != null) {
             return;
         }

         // MASTER ID 체크
         checkMasterId(masterId);

         // MASTER 권한 체크
         checkMaster(roleCheck);


         super.delete(masterId);

         // 외부 인증에서 삭제 처리
         identityProvider.withdraw(id.id());

         // 직원 퇴사 후 후속 처리
         userEvents.deleted(this);
     }

     // 활성화 사용자 여부(재직중 직원 여부)
     public boolean isEnabled() {
        return this.approved && this.deletedAt == null;
     }

     // MASTER 관리자 ID 체크
     private void checkMasterId(String masterId) {
         if (!StringUtils.hasText(masterId)) {
             throw new BadRequestException("관리자 아이디가 누락되었습니다.");
         }
     }

     // MASTER 권한 체크
     private void checkMaster(RoleCheck roleCheck) {
         if (roleCheck.hasRole(MASTER)) {
             throw new ForbiddenException(MASTER.getDescription() + " 권한이 필요합니다.");
         }
     }
}
