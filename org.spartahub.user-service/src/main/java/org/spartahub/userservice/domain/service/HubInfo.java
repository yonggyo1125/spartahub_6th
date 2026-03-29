package org.spartahub.userservice.domain.service;

import org.spartahub.userservice.domain.Hub;

import java.util.UUID;

public interface HubInfo {
    Hub get(UUID hubId);
}
