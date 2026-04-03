package org.spartahub.hubservice.infrastructure.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.spartahub.hubservice.domain.hub.query.HubQueryRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

import static org.spartahub.hubservice.domain.hub.QHubItem.hubItem;

@Repository
@RequiredArgsConstructor
public class HubQueryRepositoryImpl implements HubQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean isDuplicatedItem(UUID storeId, String code) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(hubItem)
                .where(
                        hubItem.store.id.eq(storeId),
                        hubItem.detail.code.eq(code),
                        hubItem.deletedAt.isNull()
                )
                .fetchFirst();
        return fetchOne != null;
    }
}
