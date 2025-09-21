package com.example.rpgengine.domain.session;

import com.example.rpgengine.domain.session.valueobject.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@AllArgsConstructor
public class Session {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "owner_id", nullable = false, updatable = false))
    private UserId ownerId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "estimated_duration", nullable = false)
    private Short estimatedDurationInMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;

    @Column(name = "min_players", nullable = false)
    private int minPlayers = 1;

    @Column(name = "max_players", nullable = false)
    private int maxPlayers = 10;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.DRAFT;

    @Column(name = "invite_code")
    private String inviteCode;

    @ElementCollection
    @CollectionTable(
            name = "session_participants",
            joinColumns = @JoinColumn(name = "session_id")
    )
    private Set<SessionParticipant> participants = new HashSet<>();

    public Session(
            UserId ownerId,
            String description,
            LocalDateTime startDate,
            Short estimatedDurationInMinutes,
            DifficultyLevel difficulty,
            Visibility visibility,
            Integer minPlayers,
            Integer maxPlayers
    ) {
        this.ownerId = ownerId;
        this.description = description;
        this.startDate = startDate;
        this.estimatedDurationInMinutes = estimatedDurationInMinutes;
        this.difficulty = difficulty;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.visibility = visibility;

        if (Visibility.PRIVATE.equals(visibility)) {
            this.inviteCode = String.valueOf(new Random().nextInt(100_000, 999_999));
        }
    }


    protected Session() {
        // JPA
    }
}
