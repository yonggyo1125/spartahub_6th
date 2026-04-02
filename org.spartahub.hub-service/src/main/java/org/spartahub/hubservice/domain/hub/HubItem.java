package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.spartahub.common.domain.BaseUserEntity;
import org.spartahub.common.exception.BadRequestException;
import org.spartahub.hubservice.domain.hub.service.StoreProvider;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Getter
@Entity @ToString
@Table(name="P_HUB_ITEM")
@Access(AccessType.FIELD)
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubItem extends BaseUserEntity {
    @EmbeddedId
    private ItemId id;

    @Version
    private int version;

    @Enumerated(EnumType.STRING)
    @Column(length=15, nullable=false)
    private ItemStatus status;

    private String itemCode; // 업체에서 관리하는 상품 코드, 상품 정보 업데이트 기준

    @Column(length=150, name="item_name")
    private String name;

    @Column(name="item_stock")
    private int stock; // 재고

    @Embedded
    private Store store;

    @Builder
    protected HubItem(String name, int stock, UUID storeId, StoreProvider storeProvider) {
        if (!StringUtils.hasText(name)) {
            throw new BadRequestException("상품명은 필수입력 값 입니다.");
        }

        if (stock < 0) {
            throw new BadRequestException("재고 수량은 음수일 수 없습니다.");
        }

        this.id = ItemId.of();
        this.stock = stock;
        this.name = name.trim();
        this.store = new Store(storeId, storeProvider);

        // 최초 상품 등록시 상품 준비중 상태(주문 불가)
        changePreparing();
    }

    // 상품명 변경


    // 판매중 상태로 변경
    public void changeOnSale() {
        if (this.stock < 1) {
            throw new BadRequestException("재고가 없는 상품은 판매 중 상태로 변경할 수 없습니다.");
        }

        this.status = ItemStatus.ON_SALE;
    }

    // 품절 상태로 변경(재고가 있더라도 임의로 품절 처리하여 판매를 막는 경우도 있음)
    public void changeOutStock() {
        this.status = ItemStatus.OUT_OF_STOCK;
    }

    // 판매준비중 상태로 변경
    public void changePreparing() {
        this.status = ItemStatus.PREPARING;
    }

    // 재고 차감
    public void reduceStock(int quantity) {
        if (this.stock < quantity) {
            throw new BadRequestException("재고가 부족합니다. 현재 재고: " + this.stock);
        }
        this.stock -= quantity;

        // 재고가 0이 되면 품절 처리
        if (this.stock == 0) {
            this.changeOutStock();
        }
    }

    // 재고 추가
    public void addStock(int quantity) {
        if (quantity < 1) {
            throw new BadRequestException("추가할 재고는 1개 이상이어야 합니다.");
        }
        this.stock += quantity;
    }
}
