package com.example.rpgengine.domain.user.event;


import com.example.rpgengine.domain.user.valueobject.Email;

import java.util.UUID;

public record UserRegisteredEvent(UUID userId, String username, Email email, String activationToken) {
}
