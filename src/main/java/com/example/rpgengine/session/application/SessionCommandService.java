package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.JoinSessionPolicyFactory;
import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.exception.SessionForbiddenException;
import com.example.rpgengine.session.domain.exception.SessionInvalidUserException;
import com.example.rpgengine.session.domain.exception.SessionNotFoundException;
import com.example.rpgengine.session.domain.port.in.SessionCommandServicePort;
import com.example.rpgengine.session.domain.port.in.command.*;
import com.example.rpgengine.session.domain.port.out.SessionRepositoryPort;
import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Transactional
class SessionCommandService implements SessionCommandServicePort {
    private final SessionRepositoryPort sessionRepositoryPort;
    private final UserPort userPort;
    private final ApplicationEventPublisher eventPublisher;

    public SessionCommandService(
            SessionRepositoryPort sessionRepositoryPort,
            UserPort userPort,
            ApplicationEventPublisher eventPublisher
    ) {
        this.sessionRepositoryPort = sessionRepositoryPort;
        this.userPort = userPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public SessionId createSession(CreateSessionCommand createSessionCommand) {
        var sessionUser = userPort
                .getById(createSessionCommand.owner())
                .orElseThrow(SessionInvalidUserException::new);

        var session = new Session(
                sessionUser.id(),
                createSessionCommand.title(),
                createSessionCommand.description(),
                createSessionCommand.startDate(),
                Duration.ofMinutes((long) createSessionCommand.durationInMinutes()),
                createSessionCommand.difficultyLevel(),
                createSessionCommand.visibility(),
                createSessionCommand.minPlayers(),
                createSessionCommand.maxPlayers()
        );

        var storedSession = sessionRepositoryPort.save(session);

        session.getDomainEvents().forEach(eventPublisher::publishEvent);

        return storedSession.getId();
    }

    @Override
    public void join(JoinSessionCommand joinSessionCommand) {
        var session = sessionRepositoryPort
                .findById(joinSessionCommand.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        var joinPolicy = JoinSessionPolicyFactory.createJoinPolicy(joinSessionCommand.inviteCode());
        session.join(joinSessionCommand.userId(), joinPolicy);

        sessionRepositoryPort.save(session);

        session.getDomainEvents().forEach(eventPublisher::publishEvent);
    }

    @Override
    public void handleUserJoinRequest(HandleUserJoinSessionDecisionCommand cmd) {
        var session = sessionRepositoryPort
                .findById(cmd.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        if (!session.canUserApproveJoinRequests(cmd.ownerId())) {
            throw new SessionForbiddenException("user can't approve join requests");
        }

        if (cmd.shouldApproveRequest()) {
            session.approveJoinRequest(cmd.userId());
        } else {
            session.rejectJoinRequest(cmd.userId());
        }

        sessionRepositoryPort.save(session);

        session.getDomainEvents().forEach(eventPublisher::publishEvent);
    }

    @Override
    public void updateSession(UpdateSessionCommand cmd) {
        var sessionUser = userPort
                .getById(cmd.ownerId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionRepositoryPort
                .findById(cmd.sessionId())
                .orElseThrow(SessionNotFoundException::new);

        if (!session.isOwner(sessionUser.id())) {
            throw new SessionForbiddenException("only owner can update session");
        }

        session.update(
                cmd.title(),
                cmd.description(),
                cmd.startDate(),
                Duration.ofMinutes((long) cmd.durationInMinutes()),
                cmd.difficultyLevel(),
                cmd.visibility(),
                cmd.minPlayers(),
                cmd.maxPlayers()
        );

        sessionRepositoryPort.save(session);

        session.getDomainEvents().forEach(eventPublisher::publishEvent);
    }

    @Override
    public void scheduleSession(ScheduleSessionCommand cmd) {
        var sessionUser = userPort
                .getById(cmd.userId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionRepositoryPort
                .findById(cmd.id())
                .orElseThrow(SessionNotFoundException::new);

        if (!session.isOwner(sessionUser.id())) {
            throw new SessionForbiddenException("only owner can schedule session");
        }

        session.scheduleSession();

        sessionRepositoryPort.save(session);

        session.getDomainEvents().forEach(eventPublisher::publishEvent);
    }

    @Override
    public void deleteSession(DeleteSessionCommand deleteSessionCommand) {
        var sessionUser = userPort
                .getById(deleteSessionCommand.userId())
                .orElseThrow(SessionInvalidUserException::new);

        var session = sessionRepositoryPort
                .findById(deleteSessionCommand.id())
                .orElseThrow(SessionNotFoundException::new);

        if (!session.isGameMaster(sessionUser.id()) && !session.isOwner(sessionUser.id())) {
            throw new SessionForbiddenException("user can't delete session");
        }

        session.delete();
        sessionRepositoryPort.delete(session);

        session.getDomainEvents().forEach(eventPublisher::publishEvent);
    }
}
