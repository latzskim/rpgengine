package com.example.rpgengine.session.domain.valueobject;

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

    public SessionParticipant(UserId userId, ParticipantRole role, CharacterId characterId) {
        this.userId = userId;
        this.role = role;
        this.characterId = characterId;
    }

    public SessionParticipant(UserId userId, ParticipantRole role) {
        this.userId = userId;
        this.role = role;
    }

    protected SessionParticipant() {
        // jpa
    }

    public void assignCharacter(CharacterId characterId) {
        this.characterId = characterId;
    }
}