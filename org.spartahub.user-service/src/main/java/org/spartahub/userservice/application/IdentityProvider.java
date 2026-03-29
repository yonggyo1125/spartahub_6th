package org.spartahub.userservice.application;

import java.util.UUID;

public interface IdentityProvider {

    // 원격 회원 등록
    UUID register(String email, String password);

    // 원격 회원 등록 취소
    void withdraw(UUID userId);
}
