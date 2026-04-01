package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;
import org.spartahub.common.domain.BaseUserEntity;


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

    @Column(length=45, nullable=false)
    public String name; // 허브명

    

}
