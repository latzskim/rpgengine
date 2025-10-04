package com.example.rpgengine.domain.session.valueobject;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;

import java.util.UUID;

@Embeddable
public class SessionId {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    protected SessionId() {
        // jpa
    }

    public SessionId(UUID id) {
        this.id = id;
    }
}
