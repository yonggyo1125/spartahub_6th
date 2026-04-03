package org.spartahub.hubservice.domain.hub.query;

import java.util.UUID;

public interface HubQueryRepository {
    boolean isDuplicatedItem(UUID storeId, String code);
}
