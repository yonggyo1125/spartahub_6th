package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record HubId(
        @JdbcTypeCode(SqlTypes.UUID)
        @Column(length=36, name="hub_id")
        UUID id
) implements Serializable {
    public static HubId of(UUID id) {
        return new HubId(id);
    }

    public static HubId of() {
        return new HubId(UUID.randomUUID());
    }

    public static HubId fromString(String id) {
        return new HubId(UUID.fromString(id));
    }
}
