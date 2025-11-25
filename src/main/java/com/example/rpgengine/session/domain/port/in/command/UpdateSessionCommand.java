package com.example.rpgengine.session.domain.port.in.command;

import com.example.rpgengine.session.domain.valueobject.DifficultyLevel;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.UserId;
import com.example.rpgengine.session.domain.valueobject.Visibility;

import java.time.LocalDateTime;

public record UpdateSessionCommand(
        SessionId sessionId,
        UserId ownerId,
        String title,
        String description,
        LocalDateTime startDate,
        Integer durationInMinutes,
        DifficultyLevel difficultyLevel,
        Visibility visibility,
        Integer minPlayers,
        Integer maxPlayers
) {
}
