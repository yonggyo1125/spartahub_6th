package org.spartahub.userservice.application;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.spartahub.userservice.application.dto.UserDto;
import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserRepository;
import org.spartahub.userservice.domain.service.HubInfo;
import org.spartahub.userservice.domain.service.StoreInfo;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSignUpService {

    private final IdentityProvider identityProvider;
    private final UserRepository userRepository;
    private final HubInfo hubInfo;
    private final StoreInfo storeInfo;

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
                    .hubInfo(hubInfo)
                    .storeInfo(storeInfo)
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
}
