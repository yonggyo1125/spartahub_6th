package org.spartahub.userservice.domain.service;

// 허브 배송 담당자를 승인 할 때 다음 배송 순번 결정(수정시에는 변경 불가, 최초 한번 등록)
public interface DeliveryRotationGenerator {
    int next();
}
