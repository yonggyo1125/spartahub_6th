package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.spartahub.common.domain.BaseUserEntity;
import org.spartahub.hubservice.domain.hub.event.HubEvents;
import org.spartahub.hubservice.domain.hub.exception.MasterOnlyException;
import org.spartahub.hubservice.domain.hub.service.AddressResolver;
import org.spartahub.hubservice.domain.hub.service.RoleCheck;

import java.util.List;


/**
 * 1. 허브 관리는 마스터 관리자(MASTER), 허브 관리자(HUB_MANAGER)에 의해 이루어 지며
 *      - 허브의 추가, 수정, 삭제는 마스터 관리자(MASTER)에게 일임되어 있음
 *      - 배송 담당자(HUB_DELIVERY)의 배송 순번에 따른 배차는 허브 관리자(HUB_MANAGER)에 의해 이뤄지게 된다.
 *      - 마스터 관리자(MASTER)외 허브 관리자(HUB_MANAGER), 허브 배송관리자(HUB_DELIVERY), 업체 배송 담당자(STORE_DELIVERY), 업체 담당자(COMPANY)는 허브에 대한 조회 권한만 가진다.
 *
 * 2. 허브는 전국 17개가 존재하여, 추가, 수정, 삭제가 가능해야 한다.
 * 3. 허브간 최적의 이동 경로를 산출하게 되므로 그 기반이 되는 주소, 위도, 경도는 필수
 * 4. 허브가 추가, 수정, 삭제가 되면 허브 경로 역시 재산출 되어야 한다(Route)
 */
@Getter
@Entity @ToString
@Table(name="P_HUB")
@Access(AccessType.FIELD)
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hub extends BaseUserEntity {
    @EmbeddedId
    private HubId id;

    @Version
    private int version;

    @Column(name="hub_name", length=45, nullable=false)
    private String name; // 허브명

    @Embedded
    private HubLocation location;

    // 허브에서 관리하는 상품
    @OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "hub_id")
    private List<HubItem> items;

    @Builder
    public Hub(String name, String address, AddressResolver addressResolver, RoleCheck roleCheck, HubEvents events) {

        // 권한 체크
        checkAuthority(roleCheck);

        this.id = HubId.of();

        this.name = name; // 허브명

        // 허브 주소 및 위도, 경도 처리
        this.location = new HubLocation(address, addressResolver);

        // 허브 변경 후속 처리 이벤트 호출
        events.hubChanged(id);
    }

    // 허브 이름 변경
    public void changeName(String name, RoleCheck roleCheck) {
        checkAuthority(roleCheck);

        this.name = name;
    }

    // 허브 주소 변경
    public void changeAddress(String address, AddressResolver resolver, RoleCheck roleCheck, HubEvents events) {
        checkAuthority(roleCheck);
        
        // 주소가 이전과 동일하다면 처리하지 않음
        if (this.location.getAddress() != null && this.location.getAddress().equals(address)) {
            return;
        }

        this.location = new HubLocation(address, resolver);

        // 주소 변경 후 후속 처리를 위한 이벤트 호출
        events.hubChanged(id);
    }

    // 허브 삭제
    // 허브가 삭제되면 허브간 경로 조합 및 허브에 소속된 상품을 삭제합니다(삭제 후속 처리)
    public void delete(RoleCheck roleCheck, HubEvents events) {
        checkAuthority(roleCheck);

        // 이미 삭제된 경우는 처리하지 않음
        if (this.deletedAt != null) return;

        super.delete(null); // deletedBy에는 MASTER 권한을 가진 로그인 사용자의 이메일로 업데이트 된다.

        // 삭제 후속 처리를 위한 이벤트 호출
        events.hubChanged(id);
    }

    // 허브 등록 수정, 삭제는 MASTER 관리자로 한정
    private void checkAuthority(RoleCheck roleCheck) {
        if (!roleCheck.hasRole(UserType.MASTER)) {
            throw new MasterOnlyException();
        }
    }
}
