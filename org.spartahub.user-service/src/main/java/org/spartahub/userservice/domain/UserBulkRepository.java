package org.spartahub.userservice.domain;

public interface UserBulkRepository {
    long bulkUpdateHubInfo(Hub hub);
    long bulkUpdateStoreInfo(Store store);
}
