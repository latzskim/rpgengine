package com.example.rpgengine.user.domain.valueobject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Email {
    @Column(name = "email", nullable = false, unique = true)
    private String value;

    protected Email() {
    }

    public Email(String value) {
        if (value == null || !value.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.value = value.toLowerCase();
    }

    public String getUsername(int randSuffix) {
        return this.value.split("@")[0] + randSuffix;
    }
}