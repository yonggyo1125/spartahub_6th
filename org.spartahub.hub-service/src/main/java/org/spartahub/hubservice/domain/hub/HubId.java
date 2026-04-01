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
        @Column(length=36, name="user_id")
        UUID id
) implements Serializable {
    public static HubId of(UUID id) {
        return new HubId(id);
    }

    public static HubId of() {
        return new HubId(UUID.randomUUID());
    }

    public static HubId fromString(String sub) {
        return new HubId(UUID.fromString(sub));
    }
}
