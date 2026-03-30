package org.spartahub.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.spartahub.common.exception.BadRequestException;
import org.spartahub.userservice.domain.exception.StoreNotFoundException;
import org.spartahub.userservice.domain.service.StoreData;
import org.spartahub.userservice.domain.service.StoreProvider;

import java.util.UUID;

@Embeddable
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Store {
    @JdbcTypeCode(SqlTypes.UUID)
    @Column(length = 36, name = "store_id")
    private UUID id;

    @Column(length = 45, name = "store_name")
    private String name;

    @Column(name = "store_address")
    private String address;

    protected Store(UUID id, StoreProvider storeProvider) {

        if (id == null || storeProvider == null) {
            throw new BadRequestException("소속 업체 등록/수정을 위한 필수 항목이 누락되었습니다.");
        }

        StoreData store = storeProvider.get(id);
        if (store == null) {
            throw new StoreNotFoundException(id);
        }


        this.id = id;
        name = store.name();
        address = store.address();
    }
}
