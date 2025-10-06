package com.example.rpgengine.session.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class CharacterId {
    @Column(name = "character_id")
    private Integer id;

    public CharacterId(Integer id) {
        this.id = id;
    }

    protected CharacterId() {
        // JPA
    }
}