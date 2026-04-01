package org.spartahub.hubservice.domain.hub;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record ItemId(
        @JdbcTypeCode(SqlTypes.UUID)
        @Column(length=36, name="user_id")
        UUID id
) implements Serializable {
    public static ItemId of(UUID id) {
        return new ItemId(id);
    }

    public static ItemId of() {
        return new ItemId(UUID.randomUUID());
    }

    public static ItemId fromString(String id) {
        return new ItemId(UUID.fromString(id));
    }
}