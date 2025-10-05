package com.example.rpgengine.session.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
public class UserId {
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    private UserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId cannot be null or blank");
        }

        this.userId = UUID.fromString(value);
    }

    private UserId(UUID value) {
        this.userId = value;
    }

    protected UserId() {
        // JPA
    }

    public static UserId fromString(String value) {
        return new UserId(value);
    }
    public static UserId fromUUID(UUID value) {
        return new UserId(value);
    }
}
