package com.example.rpgengine.session.domain.port.out;

import com.example.rpgengine.session.domain.valueobject.SessionUser;
import com.example.rpgengine.session.domain.valueobject.UserId;

import java.util.Optional;

public interface UserPort {
    Optional<SessionUser> getById(UserId userId);
}
