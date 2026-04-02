package org.spartahub.userservice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.spartahub.common.response.CommonResponse;
import org.spartahub.userservice.domain.service.HubData;
import org.spartahub.userservice.domain.service.HubProvider;
import org.spartahub.userservice.infrastructure.client.HubClient;
import org.spartahub.userservice.infrastructure.client.dto.HubResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubProviderImpl implements HubProvider {

    private final HubClient client;

    @Override
    public HubData get(UUID hubId) {
        CommonResponse<HubResponse> res = client.getHub(hubId);
        return res == null || res.data() == null || res.data().id() == null ? null : new HubData(res.data().name(), res.data().address());
    }
}
