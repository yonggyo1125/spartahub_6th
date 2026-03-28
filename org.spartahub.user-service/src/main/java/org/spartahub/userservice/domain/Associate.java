package org.spartahub.userservice.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

/**
 * 허브와 관련이 있는 직원(업체 배송 담당자, 업체 담당자)라면 허브 정보를 기록하고
 * 업체 담당자라면 소속된 업체가 있으므로 업체 정보를 기록한다.
 * 즉, 업체 배송 담당자는 허브 정보만 있으며, 업체 담당자는 허브 + 업체 정보를 가지고 있다.
 * @param hub
 * @param store
 */
@Embeddable
record Associate(
        @Embedded
        Hub hub,

        @Embedded
        Store store
) {}
