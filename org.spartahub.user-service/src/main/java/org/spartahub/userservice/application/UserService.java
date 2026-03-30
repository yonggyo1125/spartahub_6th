package org.spartahub.userservice.application;

import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.application.dto.UserDto;
import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserRepository;
import org.spartahub.userservice.domain.exception.UserNotFoundException;
import org.spartahub.userservice.domain.service.HubProvider;
import org.spartahub.userservice.domain.service.IdentityProvider;
import org.spartahub.userservice.domain.service.RoleCheck;
import org.spartahub.userservice.domain.service.StoreProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IdentityProvider identityProvider;
    private final UserRepository userRepository;
    private final HubProvider hubProvider;
    private final StoreProvider storeProvider;
    private final RoleCheck roleCheck;

    /**
     * 1. 외부 인증시스템에 계정 생성 및 UUID 발급
     * 2. 발급받은 UUID로 User 엔티티 생성 및 DB 저장
     * 3. DB 저장 실패 시 Keycloak에 생성된 계정 롤백(Withdraw)
     */
    @Transactional
    public UUID signUp(UserDto.SignUp data) {

        UUID userId = identityProvider.register(data.getEmail(), data.getPassword());

        try {
            User user = User.builder()
                    .id(UserId.of(userId))
                    .name(data.getName())
                    .type(data.getType())
                    .hubId(data.getHubId())
                    .storeId(data.getStoreId())
                    .hubProvider(hubProvider)
                    .storeInfo(storeProvider)
                    .email(data.getEmail())
                    .slackId(data.getSlackId())
                    .build();
            return userRepository.save(user).getId().id();
        } catch (Exception e) {
            // 보상 트랜잭션 - 외부인증 시스템 역시 계정 삭제
            identityProvider.withdraw(userId);

            throw e;
        }
    }

    @Transactional
    public void changePassword(UUID userId, String password) {
        User user = userRepository.findById(UserId.of(userId)).orElseThrow(UserNotFoundException::new);

        user.changePassword(password, roleCheck, identityProvider);
    }
}
