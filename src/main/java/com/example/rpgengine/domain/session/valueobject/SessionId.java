package com.example.rpgengine.domain.session.valueobject;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class SessionId {
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    protected SessionId() {
        // jpa
    }

    public SessionId(UUID id) {
        this.id = id;
    }
}
