package com.example.rpgengine.domain.session;

import com.example.rpgengine.domain.session.event.SessionGMAssigned;
import com.example.rpgengine.domain.session.event.SessionUserJoinRequested;
import com.example.rpgengine.domain.session.event.SessionUserJoined;
import com.example.rpgengine.domain.session.exception.SessionGameMasterAlreadyAssignedException;
import com.example.rpgengine.domain.session.exception.SessionPrivateException;
import com.example.rpgengine.domain.session.valueobject.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "sessions")
@AllArgsConstructor
@Getter
public class Session {
    @EmbeddedId
    private SessionId id;

    @Embedded
    @AttributeOverride(name = "userId", column = @Column(name = "owner_id", nullable = false, updatable = false))
    private UserId ownerId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "estimated_duration", nullable = false)
    private Long estimatedDurationInMinutes;

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


    @ElementCollection
    @CollectionTable(
            name = "join_requests",
            joinColumns = @JoinColumn(name = "session_id")
    )
    private Set<JoinRequest> joinRequests = new HashSet<>();

    @Transient
    private List<Object> domainEvents = new ArrayList<>();

    public Session(
            UserId ownerId,
            String description,
            LocalDateTime startDate,
            Duration duration,
            DifficultyLevel difficulty,
            Visibility visibility,
            Integer minPlayers,
            Integer maxPlayers
    ) {
        this.ownerId = ownerId;
        this.description = description;
        this.startDate = startDate;
        this.estimatedDurationInMinutes = duration.toMinutes();
        this.difficulty = difficulty;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.visibility = visibility;
        this.inviteCode = String.valueOf(new Random().nextInt(100_000, 999_999));

        var gmParticipant = new SessionParticipant(ownerId, ParticipantRole.GAMEMASTER);
        this.participants.add(gmParticipant);
    }

    protected Session() {
        // JPA
    }

    // Returns copy of participants
    public Set<SessionParticipant> getParticipants() {
        return Set.copyOf(this.participants);
    }

    public void assignGameMaster(UserId gmId) {
        for (var participant : participants) {
            if (participant.getRole().equals(ParticipantRole.GAMEMASTER)) {
                throw new SessionGameMasterAlreadyAssignedException();
            }
        }

        var gmParticipant = new SessionParticipant(gmId, ParticipantRole.GAMEMASTER);
        this.participants.add(gmParticipant);
        this.domainEvents.add(new SessionGMAssigned(this.id, gmId));
    }

    public List<Object> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    protected void addParticipant(UserId userId) {
        var playerParticipant = new SessionParticipant(userId, ParticipantRole.PLAYER);
        this.participants.add(playerParticipant);
        this.domainEvents.add(new SessionUserJoined(this.id, userId));
    }

    protected void joinRequest(UserId joinUserId) {
        var joinReq = new JoinRequest(joinUserId);
        this.joinRequests.add(joinReq);
        this.domainEvents.add(new SessionUserJoinRequested(this.id, joinUserId));
    }

    public void join(UserId userId, JoinPolicy invitePolicy) {
        invitePolicy.join(this, userId);
    }
}
