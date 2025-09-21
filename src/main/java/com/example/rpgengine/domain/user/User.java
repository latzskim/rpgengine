package com.example.rpgengine.domain.user;


import com.example.rpgengine.domain.user.event.UserRegisteredEvent;
import com.example.rpgengine.domain.user.valueobject.Email;
import com.example.rpgengine.domain.user.valueobject.Status;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
public class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Embedded
    private Email email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "moderation_id")
    private Integer moderationId;

    @Column(name = "password_hash", nullable = false, unique = true)
    String passwordHash;

    @Column(name = "activation_token", unique = true)
    String activationToken;

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    protected User() {
        // JPA
    }

    public User(Email email, String passwordHash) {
        this.id = UUID.randomUUID();
        this.username = email.getUsername(new Random().nextInt(100));
        this.passwordHash = passwordHash;
        this.activationToken = UUID.randomUUID().toString();
        this.email = email;
        this.status = Status.ACTIVATION_PENDING;
        this.createdAt = Instant.now();


        this.domainEvents.add(new UserRegisteredEvent(
                this.getId(),
                this.getUsername(),
                this.getEmail(),
                this.getActivationToken()
        ));
    }


    public List<Object> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    public void activate() {
        if (this.getStatus() != Status.ACTIVATION_PENDING) {
            throw new IllegalStateException("User cannot be activated due to illegal state");
        }

        this.status = Status.ACTIVE;
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }
}