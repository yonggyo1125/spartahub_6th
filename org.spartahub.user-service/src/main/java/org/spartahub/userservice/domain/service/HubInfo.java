package org.spartahub.userservice.domain.service;

import java.util.UUID;

public interface HubInfo {
    HubData get(UUID hubId);
}
