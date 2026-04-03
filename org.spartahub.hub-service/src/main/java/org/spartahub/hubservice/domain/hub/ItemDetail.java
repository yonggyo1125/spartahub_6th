package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.spartahub.common.exception.BadRequestException;
import org.spartahub.hubservice.domain.hub.exception.ItemDuplicatedException;
import org.spartahub.hubservice.domain.hub.exception.ItemNotFoundException;
import org.spartahub.hubservice.domain.hub.service.ItemProvider;
import org.spartahub.hubservice.domain.hub.service.dto.ItemData;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Getter
@ToString
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemDetail {
    @Column(length=36, name="item_code", nullable = false)
    private String code; // 업체 상품 코드

    @Column(length=150, name="item_name")
    private String name;

    @Column(name="item_memo")
    private String memo; // 상품 메모

    protected ItemDetail(UUID storeId, String code, ItemProvider provider) {
        if (storeId == null) {
            throw new BadRequestException("업체 ID는 필수입력 값 입니다.");
        }

        if (!StringUtils.hasText(code)) {
            throw new BadRequestException("상품 코드는 필수입력 값 입니다.");
        }

        if (provider == null) {
            throw new BadRequestException("업체 상품 Provider가 누락되었습니다.");
        }

        // 중복 상품 여부 체크
        if (provider.isDuplicated(storeId, code)) {
            throw new ItemDuplicatedException(code);
        }

        ItemData data = provider.getItem(storeId, code);
        if (data == null || !StringUtils.hasText(data.code())) {
            throw new ItemNotFoundException(storeId, code);
        }

        this.code = data.code();
        this.name = data.name();
        this.memo = data.memo();
    }

}
