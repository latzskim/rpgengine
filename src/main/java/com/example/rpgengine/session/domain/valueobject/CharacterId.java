package com.example.rpgengine.session.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterId {
    @Column(name = "character_id")
    private Integer characterId;

    public CharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    protected CharacterId() {
        // JPA
    }
}