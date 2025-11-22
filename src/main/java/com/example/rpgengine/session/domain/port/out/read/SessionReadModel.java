package com.example.rpgengine.session.domain.port.out.read;

import com.example.rpgengine.session.domain.valueobject.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_read_model")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionReadModel {
    @EmbeddedId
    private SessionId id;

    @Column(name = "title")
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SessionStatus status;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "owner_id", nullable = false, updatable = false))
    private UserId ownerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    private UserReadModel owner;

    @Column(name = "description")
    private String description;

    @Column(name = "approved_players")
    private int approvedPlayers = 0;

    @Column(name = "pending_invites")
    private int pendingInvites = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private DifficultyLevel difficulty;

    @Column(name = "estimated_duration", nullable = false)
    private Long estimatedDurationInMinutes;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "start_date")
    private LocalDateTime startDate;
}