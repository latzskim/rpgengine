package com.example.rpgengine.domain.session.valueobject;

import jakarta.persistence.*;
import lombok.Getter;

@Embeddable
@Getter
public class SessionParticipant {
    @Embedded
    private UserId userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ParticipantRole role;

    @Embedded
    private CharacterId characterId;
}