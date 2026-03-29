package org.spartahub.userservice.infrastructure.persistence;

import org.spartahub.userservice.domain.User;
import org.spartahub.userservice.domain.UserId;
import org.spartahub.userservice.domain.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends UserRepository, JpaRepository<User, UserId> {
}
