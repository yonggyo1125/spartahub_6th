package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity @ToString
@Table(name="P_HUB_ITEM")
@Access(AccessType.FIELD)
@SQLRestriction("deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HubItem {
    @EmbeddedId
    private ItemId id;

    @Version
    private int version;

    private String name;

    private int stock; // 재고

    
}
