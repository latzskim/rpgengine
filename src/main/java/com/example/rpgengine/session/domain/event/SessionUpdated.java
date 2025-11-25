package com.example.rpgengine.session.domain.event;

import com.example.rpgengine.session.domain.valueobject.DifficultyLevel;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.Visibility;

import java.time.LocalDateTime;

public record SessionUpdated(
        SessionId sessionId,
        String title,
        String description,
        LocalDateTime startDate,
        Long estimatedDurationInMinutes,
        DifficultyLevel difficulty,
        Visibility visibility,
        Integer minPlayers,
        Integer maxPlayers
) {
}
