package org.spartahub.userservice.domain.query;

import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface UserQueryRepository {
    Optional<User> findById(UserId id);
    Page<User> findAllByHubId(UUID hubId, UserQueryDto.Search search, Pageable pageable);
    Page<User> findAllByStoreId(UUID storeId, UserQueryDto.Search search, Pageable pageable);
    Page<User> findAllByType(UserType type, UserQueryDto.Search search, Pageable pageable);
    Page<User> findAll(UserQueryDto.Search search, Pageable pageable);
}
