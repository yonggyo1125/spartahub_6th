package org.spartahub.userservice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.Hub;
import org.spartahub.userservice.domain.service.HubInfo;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubInfoImpl implements HubInfo {
    @Override
    public Hub get(UUID hubId) {
        return null;
    }
}
