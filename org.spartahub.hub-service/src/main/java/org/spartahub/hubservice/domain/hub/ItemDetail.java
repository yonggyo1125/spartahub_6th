package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetail {
    @Column(length=36, name="item_code", nullable = false, unique = true)
    private String code; // 업체 상품 코드

    @Column(length=150, name="item_name")
    private String name;

    @Column(name="item_memo")
    private String memo; // 상품 메모
}
