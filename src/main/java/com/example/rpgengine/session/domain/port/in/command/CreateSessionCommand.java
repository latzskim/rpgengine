package com.example.rpgengine.session.domain.port.in.command;

import com.example.rpgengine.session.domain.valueobject.DifficultyLevel;
import com.example.rpgengine.session.domain.valueobject.UserId;
import com.example.rpgengine.session.domain.valueobject.Visibility;

import java.time.LocalDateTime;

public record CreateSessionCommand(
        UserId owner,
        String description,
        LocalDateTime startDate,
        Short durationInMinutes,
        DifficultyLevel difficultyLevel,
        Visibility visibility,
        Integer minPlayers,
        Integer maxPlayers
) {
}
