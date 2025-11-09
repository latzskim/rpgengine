package com.example.rpgengine.session.domain;

import com.example.rpgengine.session.domain.event.*;
import com.example.rpgengine.session.domain.exception.SessionGameMasterAlreadyAssignedException;
import com.example.rpgengine.session.domain.exception.SessionScheduleException;
import com.example.rpgengine.session.domain.exception.SessionUserNotFound;
import com.example.rpgengine.session.domain.exception.SessionValidationException;
import com.example.rpgengine.session.domain.valueobject.*;
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
    public static final int MIN_PLAYERS = 1;
    public static final int MAX_PLAYERS = 10;
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
            joinColumns = @JoinColumn(name = "session_id"),
            indexes = {
                    @Index(name = "idx_session_participants_session_id", columnList = "session_id"),
                    @Index(name = "idx_session_participants_user_id", columnList = "user_id")
            }
    )
    private Set<SessionParticipant> participants = new HashSet<>();


    @ElementCollection
    @CollectionTable(
            name = "join_requests",
            joinColumns = @JoinColumn(name = "session_id"),
            indexes = {
                    @Index(name = "idx_join_request_session_id", columnList = "session_id"),
                    @Index(name = "idx_join_request_user_id", columnList = "user_id")
            }
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
        validatePlayersRange(minPlayers, maxPlayers);


        this.id = new SessionId(UUID.randomUUID());
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

    private static void validatePlayersRange(Integer minPlayers, Integer maxPlayers) {
        if (minPlayers == null || minPlayers < MIN_PLAYERS) {
            throw new SessionValidationException("minPlayers can't be less than 1");
        }

        if (maxPlayers == null || maxPlayers > MAX_PLAYERS) {
            throw new SessionValidationException("maxPlayers can't be greater than 10");
        }
    }

    protected Session() {
        // JPA
    }

    public List<Object> getDomainEvents() {
        return List.copyOf(domainEvents);
    }


    // Returns copy of participants
    public Set<SessionParticipant> getParticipants() {
        return Set.copyOf(this.participants);
    }

    public Set<JoinRequest> getJoinRequests() {
        return Set.copyOf(this.joinRequests);
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
        // TODO: validate if len(participants) < max
        invitePolicy.join(this, userId);
    }

    protected boolean validateInviteCode(String inviteCode) {
        return inviteCode.equals(this.inviteCode);
    }

    public boolean isPrivate() {
        return this.visibility == Visibility.PRIVATE;
    }

    public void scheduleSession() {
        if (this.status != SessionStatus.DRAFT) {
            throw new SessionScheduleException();
        }
        this.status = SessionStatus.SCHEDULED;
        this.domainEvents.add(new SessionScheduled(this.id));
    }

    public void approveJoinRequest(UserId userId) {
        // TODO: approve only if len(particiapnts) < maxPlayers
        var request = findRequest(userId);
        request.approve();

        this.addParticipant(userId);
    }

    public void rejectJoinRequest(UserId userId) {
        var request = findRequest(userId);
        request.reject();

        this.domainEvents.add(new SessionUserRejected(this.id, userId));
    }

    private JoinRequest findRequest(UserId userId) {
        return this.getJoinRequests()
                .stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst().orElseThrow(SessionUserNotFound::new);
    }

    public boolean canUserApproveJoinRequests(UserId userId) {
        // TODO: maybe in the future, both owner and GM can approve
        return this.ownerId.equals(userId);
    }
}
