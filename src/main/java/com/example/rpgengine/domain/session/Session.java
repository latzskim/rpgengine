package com.example.rpgengine.domain.session;

import com.example.rpgengine.domain.session.event.SessionGMAssigned;
import com.example.rpgengine.domain.session.valueobject.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "sessions")
@AllArgsConstructor
@Getter
public class Session {
    @Embedded
    private SessionId id;

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


    @Transient
    private List<Object> domainEvents = new ArrayList<>();

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

    // Returns copy of participants
    public Set<SessionParticipant> getParticipants() {
        return Set.copyOf(this.participants);
    }

    public void AssignGameMaster(UserId gmId) {
        var gmParticipant = new SessionParticipant(gmId, ParticipantRole.GAMEMASTER);
        this.participants.add(gmParticipant);
        this.domainEvents.add(new SessionGMAssigned(this.id, gmId));
    }

    public List<Object> getDomainEvents() {
        return List.copyOf(domainEvents);
    }
}
