package org.spartahub.userservice.domain.service;

import org.spartahub.userservice.domain.UserId;

import java.util.UUID;

public interface IdentityProvider {

    // 원격 회원 등록
    UUID register(String email, String password);

    // 원격 회원 등록 취소
    void withdraw(UUID userId);

    // 비밀번호 변경
    void changePassword(UserId id, String newPassword);
}
