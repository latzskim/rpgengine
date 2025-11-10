package com.example.rpgengine.shared.domain.event;

import java.util.UUID;

public record UserActivated(
        UUID id,
        String username,
        String displayName
) {
}
