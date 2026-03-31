package org.spartahub.userservice.infrastructure.kafka.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.common.messaging.annotation.IdempotentConsumer;
import org.spartahub.common.util.JsonUtil;
import org.spartahub.userservice.application.UserAdminService;
import org.spartahub.userservice.domain.Store;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoreUpdateListener {
    private final UserAdminService adminService;

    @Transactional
    @IdempotentConsumer("store-updated")
    @KafkaListener(topics = "${topics.store.updated}", groupId = "user-group")
    public void onUpdated(Message<String> message, Acknowledgment ack) {
        try {
            Store store = JsonUtil.fromJson(message.getPayload(), Store.class);
            if (store != null) {
                adminService.syncStoreInfo(store);
                log.info("업체 업데이트 처리 완료: storeId={}", store.getId());
            }
            ack.acknowledge();
        } catch (Exception e) {
            log.error("업체 업데이트 처리 실패:{}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(topics = "${topics.store.updated}.DLT", groupId = "user-group")
    public void handleDLT(Message<String> message, Acknowledgment ack) {
        log.error("DLT 수신: {}", message.getPayload());
        try {
            Store store = JsonUtil.fromJson(message.getPayload(), Store.class);
            log.error("업체 업데이트 최종 실패: hubId={}", store.getId());
        } catch (Exception e) {
            log.error("DLT 메시지 변환 실패: {}", message.getPayload());
        } finally {
            ack.acknowledge();
        }
    }
}
