package org.spartahub.userservice.application.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.spartahub.userservice.domain.UserType;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserServiceDto {

    @Getter
    @Builder
    public static class SignUp {
        private final String name;
        private final String password;
        private final UserType type;

        private final UUID hubId;
        private final UUID storeId;

        private final String email;
        private final String slackId;
    }
}
