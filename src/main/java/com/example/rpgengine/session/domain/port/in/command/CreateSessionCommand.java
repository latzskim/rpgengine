package com.example.rpgengine.session.domain.port.in.command;

import com.example.rpgengine.session.domain.valueobject.DifficultyLevel;
import com.example.rpgengine.session.domain.valueobject.UserId;
import com.example.rpgengine.session.domain.valueobject.Visibility;

import java.time.LocalDateTime;
import java.util.Set;

public record CreateSessionCommand(
        UserId owner,
        String title,
        String description,
        LocalDateTime startDate,
        Short durationInMinutes,
        DifficultyLevel difficultyLevel,
        Visibility visibility,
        Integer minPlayers,
        Integer maxPlayers,
        Set<String> requirements
) {
}
