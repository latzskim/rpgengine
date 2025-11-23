package com.example.rpgengine.session.domain.port.out.read;

import com.example.rpgengine.session.domain.JoinRequest;
import com.example.rpgengine.session.domain.valueobject.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "approved_players", nullable = false, columnDefinition = "varchar(1024)")
    private String approvedPlayers = ""; // Veeeery simple for now :)

    @Setter
    @Column(name = "pending_invites", nullable = false, columnDefinition = "varchar(1024)")
    private String pendingInvites = ""; // Veeeery simple for now :)

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