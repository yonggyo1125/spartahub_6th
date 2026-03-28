package org.spartahub.userservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
record Store(
        @JdbcTypeCode(SqlTypes.UUID)
        @Column(length = 36, name = "store_id")
        UUID id, // 업체 ID

        @Column(length = 45, name = "store_name")
        String name, // 업체명

        @Column(length = 100, name = "store_address")
        String address // 업체 주소
) implements Serializable {}
