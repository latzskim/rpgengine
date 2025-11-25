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
                .approvedPlayers("")
                .pendingInvites("")
                .estimatedDurationInMinutes(event.estimatedDurationInMinutes())
                .build();

        sessionReadModelRepositoryPort.save(sessionReadModel);
    }

    @EventListener
    void on(SessionUserJoinRequested event) {
        var requestToJoinUser = sessionUserReadModelRepositoryPort
                .findById(event.userId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionReadModelRepositoryPort.findById(event.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        var modifiedInvites = session.getPendingInvites() +
                requestToJoinUser.getId().getUserId().toString() +
                "#";

        session.setPendingInvites(modifiedInvites);
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

        var approvedPlayers = session.getApprovedPlayers() +
                userJoined.getId().getUserId().toString() +
                "#";

        session.setApprovedPlayers(approvedPlayers);
        removeFromPending(userJoined, session);

        sessionReadModelRepositoryPort.save(session);
    }

    @EventListener
    void on(SessionUserRejected event) {
        var userJoined = sessionUserReadModelRepositoryPort
                .findById(event.userId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionReadModelRepositoryPort.findById(event.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        removeFromPending(userJoined, session);

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

    private static void removeFromPending(UserReadModel userJoined, SessionReadModel session) {
        var userPendingId = userJoined.getId().getUserId().toString() + "#";
        var pendingInvites = session.getPendingInvites().replace(userPendingId, "");
        session.setPendingInvites(pendingInvites);
    }
}
