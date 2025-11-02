package com.example.rpgengine.session.adapter.repository;

import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.SessionUser;
import com.example.rpgengine.session.domain.valueobject.UserId;
import com.example.rpgengine.user.domain.port.out.UserRepositoryPort;
import com.example.rpgengine.user.domain.valueobject.Email;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class UserAdapter implements UserPort {
    // Do not use user repo :)
    private final UserRepositoryPort userRepository;

    UserAdapter(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<SessionUser> getById(UserId userId) {
        return userRepository
                .findById(userId.getUserId())
                .map(user -> new SessionUser(UserId.fromUUID(user.getId())));
    }

    @Override
    public Optional<SessionUser> findByUsername(String username) {
        return userRepository
                .findByEmail(new Email(username))
                .map(user -> new SessionUser(UserId.fromUUID(user.getId())));
    }
}
