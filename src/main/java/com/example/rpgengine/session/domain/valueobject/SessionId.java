package com.example.rpgengine.session.domain.valueobject;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
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
