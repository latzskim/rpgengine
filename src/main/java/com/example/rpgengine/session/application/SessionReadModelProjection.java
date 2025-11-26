package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.event.*;
import com.example.rpgengine.session.domain.exception.SessionInvalidUserException;
import com.example.rpgengine.session.domain.exception.SessionNotFoundException;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModel;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModelRepositoryPort;
import com.example.rpgengine.session.domain.port.out.read.SessionUserReadModelRepositoryPort;
import com.example.rpgengine.session.domain.port.out.read.UserReadModel;
import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import com.example.rpgengine.session.domain.valueobject.UserId;
import com.example.rpgengine.shared.domain.event.UserActivated;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
class SessionReadModelProjection {
    private final SessionReadModelRepositoryPort sessionReadModelRepositoryPort;
    private final SessionUserReadModelRepositoryPort sessionUserReadModelRepositoryPort;


    public SessionReadModelProjection(
            SessionReadModelRepositoryPort sessionReadModelRepositoryPort,
            SessionUserReadModelRepositoryPort sessionUserReadModelRepositoryPort
    ) {
        this.sessionReadModelRepositoryPort = sessionReadModelRepositoryPort;
        this.sessionUserReadModelRepositoryPort = sessionUserReadModelRepositoryPort;
    }

    @EventListener
    void on(SessionCreated event) {
        var sessionOwner = sessionUserReadModelRepositoryPort
                .findById(event.ownerId())
                .orElseThrow(SessionInvalidUserException::new);

        var sessionReadModel = SessionReadModel.builder()
                .id(event.sessionId())
                .title(event.title())
                .description(event.description())
                .ownerId(event.ownerId())
                .owner(sessionOwner)
                .status(SessionStatus.DRAFT) // TODO: should be in event?
                .startDate(event.startDate())
                .visibility(event.visibility())
                .difficulty(event.difficulty())
                .approvedPlayers(new HashSet<>())
                .pendingInvites(new HashSet<>())
                .estimatedDurationInMinutes(event.estimatedDurationInMinutes())
                .requirements(event.requirements())
                .build();

        sessionReadModelRepositoryPort.save(sessionReadModel);
    }

    @EventListener
    void on(SessionUpdated event) {
        var session = sessionReadModelRepositoryPort.findById(event.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        session.setTitle(event.title());
        session.setDescription(event.description());
        session.setStartDate(event.startDate());
        session.setEstimatedDurationInMinutes(event.estimatedDurationInMinutes());
        session.setDifficulty(event.difficulty());
        session.setVisibility(event.visibility());
        session.setRequirements(event.requirements());

        // TODO: handle min/max players

        sessionReadModelRepositoryPort.save(session);
    }

    @EventListener
    void on(SessionStatusEvent.SessionScheduled event) {
        var session = sessionReadModelRepositoryPort.findById(event.id())
                .orElseThrow(SessionNotFoundException::new);

        session.setStatus(SessionStatus.SCHEDULED);

        sessionReadModelRepositoryPort.save(session);
    }

    @EventListener
    void on(SessionUserJoinRequested event) {
        var requestToJoinUser = sessionUserReadModelRepositoryPort
                .findById(event.userId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionReadModelRepositoryPort.findById(event.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        session.getPendingInvites().add(requestToJoinUser);

        sessionReadModelRepositoryPort.save(session);
    }

    @EventListener
    void on (SessionStatusEvent.SessionHardDeleted event) {
        sessionReadModelRepositoryPort.deleteById(event.id());
    }

    @EventListener
    void on(SessionUserJoined event) {
        var userJoined = sessionUserReadModelRepositoryPort
                .findById(event.userId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionReadModelRepositoryPort.findById(event.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        session.getApprovedPlayers().add(userJoined);
        session.getPendingInvites().remove(userJoined);

        sessionReadModelRepositoryPort.save(session);
    }

    @EventListener
    void on(SessionUserRejected event) {
        var userJoined = sessionUserReadModelRepositoryPort
                .findById(event.userId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionReadModelRepositoryPort.findById(event.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        session.getPendingInvites().remove(userJoined);

        sessionReadModelRepositoryPort.save(session);
    }

    @EventListener
    void on(UserActivated event) {
        var sessionUserReadModel = UserReadModel.builder()
                .id(UserId.fromUUID(event.id()))
                .active(true)
                .avatarUrl(null)
                .displayName(event.displayName())
                .username(event.username())
                .build();

        this.sessionUserReadModelRepositoryPort.save(sessionUserReadModel);
    }


}
