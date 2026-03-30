package org.spartahub.userservice.domain.service;

import java.util.UUID;

public interface HubProvider {
    HubData get(UUID hubId);
}
