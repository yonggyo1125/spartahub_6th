package org.spartahub.userservice.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.QUser;
import org.spartahub.userservice.domain.service.DeliveryRotationGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.spartahub.userservice.domain.UserType.HUB_DELIVERY;

@Component
@RequiredArgsConstructor
public class DeliveryRotationGeneratorImpl implements DeliveryRotationGenerator {

    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional(readOnly = true)
    public int next() {
        QUser user = QUser.user;
        Integer max = queryFactory.select(user.deliveryRotationOrder)
                .from(user)
                .where(user.type.eq(HUB_DELIVERY))
                .orderBy(user.deliveryRotationOrder.desc())
                .fetchFirst();
        return max == null ? 1 : max + 1;
    }
}
