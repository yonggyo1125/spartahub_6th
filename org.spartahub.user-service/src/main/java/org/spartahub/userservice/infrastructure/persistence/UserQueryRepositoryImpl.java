package org.spartahub.userservice.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserType;
import org.spartahub.userservice.domain.query.UserQueryDto;
import org.spartahub.userservice.domain.query.UserQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.spartahub.userservice.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .where(user.id.eq(id))
                        .fetchOne()
        );
    }

    @Override
    public Page<User> findAllByHubId(UUID hubId, UserQueryDto.Search search, Pageable pageable) {
        return getPage(search, pageable, user.associate.hub.id.eq(hubId));
    }

    @Override
    public Page<User> findAllByStoreId(UUID storeId, UserQueryDto.Search search, Pageable pageable) {
        return getPage(search, pageable, user.associate.store.id.eq(storeId));
    }

    @Override
    public Page<User> findAllByType(UserType type, UserQueryDto.Search search, Pageable pageable) {
        return getPage(search, pageable, user.type.eq(type));
    }

    @Override
    public Page<User> findAll(UserQueryDto.Search search, Pageable pageable) {
        return getPage(search, pageable, null);
    }


    private Page<User> getPage(UserQueryDto.Search search, Pageable pageable, BooleanExpression baseCondition) {

        BooleanBuilder builder = createSearchCondition(search);
        if (baseCondition != null) {
            builder.and(baseCondition);
        }

        List<User> content = queryFactory
                .selectFrom(user)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc()) // 최신순 기본 정렬
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder createSearchCondition(UserQueryDto.Search search) {
        BooleanBuilder builder = new BooleanBuilder();

        if (search == null) return builder;

        // 아이디
        if (search.getIds() != null && !search.getIds().isEmpty()) {
            builder.and(user.id.in(search.getIds()));
        }

        // 허브 ID
        if (search.getHubIds() != null && !search.getHubIds().isEmpty()) {
            builder.and(user.associate.hub.id.in(search.getHubIds()));
        }

        // 업체 ID
        if (search.getStoreIds() != null && !search.getStoreIds().isEmpty()) {
            builder.and(user.associate.store.id.in(search.getStoreIds()));
        }

        // 이메일
        if (search.getEmail() != null && !search.getEmail().isEmpty()) {
            builder.and(user.contact.email.in(search.getEmail()));
        }

        // 회원명
        if (StringUtils.hasText(search.getName())) {
            builder.and(user.name.containsIgnoreCase(search.getName()));
        }

        // 허브명
        if (StringUtils.hasText(search.getHubName())) {
            builder.and(user.associate.hub.name.containsIgnoreCase(search.getHubName()));
        }

        // 매장명
        if (StringUtils.hasText(search.getStoreName())) {
            builder.and(user.associate.store.name.containsIgnoreCase(search.getStoreName()));
        }

        // 통합 키워드 검색 (name + email + hubName + storeName)
        if (StringUtils.hasText(search.getKeyword())) {
            String keyword = search.getKeyword();
            builder.and(
                    user.name.containsIgnoreCase(keyword)
                            .or(user.contact.email.containsIgnoreCase(keyword))
                            .or(user.associate.hub.name.containsIgnoreCase(keyword))
                            .or(user.associate.store.name.containsIgnoreCase(keyword))
            );
        }

        return builder;
    }
}
