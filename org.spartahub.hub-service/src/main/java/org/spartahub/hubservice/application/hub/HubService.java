package org.spartahub.hubservice.application.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spartahub.hubservice.domain.hub.Hub;
import org.spartahub.hubservice.domain.hub.HubId;
import org.spartahub.hubservice.domain.hub.HubRepository;
import org.spartahub.hubservice.domain.hub.event.HubEvents;
import org.spartahub.hubservice.domain.hub.exception.HubNotFoundException;
import org.spartahub.hubservice.domain.hub.service.AddressResolver;
import org.spartahub.hubservice.domain.hub.service.RoleCheck;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubService {
    private final HubRepository hubRepository;
    private final RoleCheck roleCheck;
    private final AddressResolver addressResolver;
    private final HubEvents hubEvents;

    // 허브 생성
    @Transactional
    public UUID create(String name, String address) {
        Hub hub = Hub.builder()
                .name(name)
                .address(address)
                .roleCheck(roleCheck)
                .addressResolver(addressResolver)
                .events(hubEvents)
                .build();
        hubRepository.save(hub);
        
        return hub.getId().id();
    }

    // 허브 삭제
    @Transactional
    public void delete(UUID hubId) {

        getHub(hubId).delete(roleCheck, hubEvents);
    }

    // 허브 이름 변경
    @Transactional
    public void changeName(UUID hubId, String newName) {
        getHub(hubId).changeName(newName, roleCheck, hubEvents);
    }

    // 허브 주소 변경
    @Transactional
    public void changeAddress(UUID hubId, String address) {
        getHub(hubId).changeAddress(address, addressResolver, roleCheck, hubEvents);
    }

    private Hub getHub(UUID hubId) {
        return hubRepository.findById(HubId.of(hubId)).orElseThrow(() -> new HubNotFoundException(hubId));
    }
}
