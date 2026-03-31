package org.spartahub.userservice.infrastructure.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.Hub;
import org.spartahub.userservice.domain.Store;
import org.spartahub.userservice.domain.UserBulkRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.spartahub.userservice.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserBulkRepositoryImpl implements UserBulkRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long bulkUpdateHubInfo(Hub hub) {
        return queryFactory.update(user)
                .set(user.associate.hub.name, hub.getName())
                .set(user.associate.hub.address, hub.getAddress())
                .set(user.modifiedAt, LocalDateTime.now())
                .set(user.modifiedBy, "SYSTEM")
                .where(user.associate.hub.id.eq(hub.getId()))
                .execute();
    }

    @Override
    public long bulkUpdateStoreInfo(Store store) {
        return queryFactory.update(user)
                .set(user.associate.store.name, store.getName())
                .set(user.associate.store.address, store.getAddress())
                .set(user.modifiedAt, LocalDateTime.now())
                .set(user.modifiedBy, "SYSTEM")
                .where(user.associate.store.id.eq(store.getId()))
                .execute();
    }
}
