package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.spartahub.common.exception.BadRequestException;
import org.spartahub.hubservice.domain.hub.exception.StoreNotFoundException;
import org.spartahub.hubservice.domain.hub.service.dto.StoreData;
import org.spartahub.hubservice.domain.hub.service.StoreProvider;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Store {
    @Column(length=36, name = "store_id", nullable = false)
    private UUID id; // 업체 ID

    @Column(length=100, name="store_name", nullable = false)
    private String name; // 업체명

    protected Store(UUID id, StoreProvider provider) {
        if (id == null) {
            throw new BadRequestException("업체 ID는 필수 입력값입니다.");
        }

        if (provider == null) {
            throw new BadRequestException("업체 정보 조회를 위한 Provider가 제공되지 않았습니다.");
        }

        StoreData data = provider.getStore(id);
        if (data == null || data.id() == null) {
            throw new StoreNotFoundException(id);
        }

        this.id = data.id();
        this.name = data.name();
    }
}
