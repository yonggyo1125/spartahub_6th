package org.spartahub.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.spartahub.common.exception.BadRequestException;
import org.spartahub.userservice.domain.exception.HubNotFoundException;
import org.spartahub.userservice.domain.service.HubInfo;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Hub implements Serializable {
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(length = 36, name = "hub_id")
    private UUID id; // 허브 ID

    @Column(length = 45, name = "hub_name")
    private String name; // 허브명

    @Column(length = 100, name = "hub_address")
    private String address; // 허브 주소

    protected Hub(UUID id, HubInfo hubInfo) {
        if (id == null || hubInfo == null) {
            throw new BadRequestException("소속 허브 등록/수정을 위한 필수 항목이 누락되었습니다.");
        }

        Hub hub = hubInfo.get(id);
        if (hub == null) {
            throw new HubNotFoundException(id);
        }

        this.id = id;
        this.name = hub.getName();
        this.address = hub.getAddress();
    }
}