package org.spartahub.hubservice.domain.hub;

import java.util.List;
import java.util.Optional;

public interface HubRepository {
    Hub save(Hub hub);
    Optional<Hub> findById(HubId hubId);
    List<Hub> findAll();
    boolean existsById(HubId hubId);
}
