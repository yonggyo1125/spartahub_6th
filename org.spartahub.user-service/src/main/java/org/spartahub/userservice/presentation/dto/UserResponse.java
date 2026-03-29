package org.spartahub.userservice.presentation.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
    @AllArgsConstructor
    public static class SignUp {
        public UUID id;
    }
}
