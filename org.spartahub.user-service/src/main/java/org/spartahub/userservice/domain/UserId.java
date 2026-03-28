package org.spartahub.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record UserId(
        @JdbcTypeCode(SqlTypes.UUID)
        @Column(length=36, name="user_id")
        UUID id
) implements Serializable {
    public static UserId of(UUID id) {
        return new UserId(id);
    }

    public static UserId of() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId fromString(String sub) {
        return new UserId(UUID.fromString(sub));
    }
}
