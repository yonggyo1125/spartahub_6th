package org.spartahub.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
record Hub(
        @JdbcTypeCode(SqlTypes.UUID)
        @Column(length = 36, name = "hub_id")
        UUID id, // 허브 ID

        @Column(length = 45, name = "hub_name")
        String name, // 허브명

        @Column(length = 100, name = "hub_address")
        String address // 허브 주소
) implements Serializable {}
