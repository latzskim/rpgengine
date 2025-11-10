package com.example.rpgengine.user.domain.event;


import com.example.rpgengine.user.domain.valueobject.Email;

import java.util.UUID;

public record UserRegistered(UUID userId, String username, Email email, String activationToken) {
}
