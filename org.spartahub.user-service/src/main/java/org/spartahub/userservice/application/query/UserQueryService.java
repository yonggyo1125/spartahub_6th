package org.spartahub.userservice.application.query;

import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserType;
import org.spartahub.userservice.domain.exception.UserNotFoundException;
import org.spartahub.userservice.domain.query.UserQueryDto;
import org.spartahub.userservice.domain.query.UserQueryRepository;
import org.spartahub.userservice.presentation.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {
    private final UserQueryRepository userQueryRepository;

    // 사용자 단건 조회
    public UserResponse.Info getUser(UUID userId) {
        return userQueryRepository.findById(UserId.of(userId))
                .map(UserResponse.Info::from)
                .orElseThrow(UserNotFoundException::new);
    }

    // 조건별 사용자 목록 조회
    public Page<UserResponse.Info> searchUsers(UserQueryDto.Search search, Pageable pageable) {
        return userQueryRepository.findAll(search, pageable)
                .map(UserResponse.Info::from);
    }

    // 특정 허브별 사용자 목록 조회
    public Page<UserResponse.Info> searchUsersByHub(UUID hubId, UserQueryDto.Search search, Pageable pageable) {
        return userQueryRepository.findAllByHubId(hubId, search, pageable)
                .map(UserResponse.Info::from);
    }

    // 특정 업체별 사용자 목록 조회
    public Page<UserResponse.Info> searchUsersByStore(UUID storeId, UserQueryDto.Search search, Pageable pageable) {
        return userQueryRepository.findAllByStoreId(storeId, search, pageable)
                .map(UserResponse.Info::from);
    }

    // 사용자 타입별 목록 조회
    public Page<UserResponse.Info> searchUsersByType(String type, UserQueryDto.Search search, Pageable pageable) {
        UserType userType = UserType.valueOf(type.toUpperCase());
        return userQueryRepository.findAllByType(userType, search, pageable)
                .map(UserResponse.Info::from);
    }
}
