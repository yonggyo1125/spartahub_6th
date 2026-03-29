package org.spartahub.userservice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.service.HubData;
import org.spartahub.userservice.domain.service.HubInfo;
import org.spartahub.userservice.infrastructure.client.HubClient;
import org.spartahub.userservice.infrastructure.client.dto.HubResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubInfoImpl implements HubInfo {

    private final HubClient client;

    @Override
    public HubData get(UUID hubId) {
        HubResponse res = client.getHub(hubId);
        return res == null || res.id() == null ? null : new HubData(res.name(), res.address());
    }
}
