package com.example.rpgengine.session.domain.valueobject;


import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Embeddable
public class JoinRequest {
    @Getter
    @Embedded
    private UserId userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JoinRequestStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    protected JoinRequest() {
        // jpa
    }

    public JoinRequest(UserId userId) {
        this.userId = userId;
        this.status = JoinRequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
}
