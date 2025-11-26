package com.example.rpgengine.session.domain.event;

import com.example.rpgengine.session.domain.valueobject.DifficultyLevel;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.UserId;
import com.example.rpgengine.session.domain.valueobject.Visibility;

import java.time.LocalDateTime;
import java.util.Set;

public record SessionCreated(
        SessionId sessionId,
        UserId ownerId,
        String title,
        String description,
        LocalDateTime startDate,
        Long estimatedDurationInMinutes,
        DifficultyLevel difficulty,
        Visibility visibility,
        Integer minPlayers,
        Integer maxPlayers,
        Set<String> requirements
) {
}
