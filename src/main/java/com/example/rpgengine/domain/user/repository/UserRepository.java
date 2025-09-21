package com.example.rpgengine.domain.user.repository;

import com.example.rpgengine.domain.user.User;
import com.example.rpgengine.domain.user.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(Email email);
    Optional<User> findByEmailAndPasswordHash(Email email, String passwordHash);
    Optional<User> findByActivationToken(String token);
}
