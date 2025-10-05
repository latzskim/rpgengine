package com.example.rpgengine.user.domain.port.out;

import com.example.rpgengine.user.domain.User;
import com.example.rpgengine.user.domain.valueobject.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

// Adapter is implemented by Sprint Data :)
public interface UserRepositoryPort extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(Email email);

    Optional<User> findByEmailAndPasswordHash(Email email, String passwordHash);

    Optional<User> findByActivationToken(String token);
}
