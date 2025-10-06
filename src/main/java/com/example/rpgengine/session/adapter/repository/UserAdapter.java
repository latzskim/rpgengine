package com.example.rpgengine.session.adapter.repository;

import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.SessionUser;
import com.example.rpgengine.session.domain.valueobject.UserId;
import com.example.rpgengine.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class UserAdapter implements UserPort {

    // User should be shared kernel but...
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
}
