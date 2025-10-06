package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.exception.SessionInvalidUserException;
import com.example.rpgengine.session.domain.port.in.SessionServicePort;
import com.example.rpgengine.session.domain.port.in.command.CreateSessionCommand;
import com.example.rpgengine.session.domain.port.out.SessionRepositoryPort;
import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
class SessionService implements SessionServicePort {
    private final SessionRepositoryPort sessionRepositoryPort;
    private final UserPort userPort;

    public SessionService(SessionRepositoryPort sessionRepositoryPort, UserPort userPort) {
        this.sessionRepositoryPort = sessionRepositoryPort;
        this.userPort = userPort;
    }

    @Transactional
    public SessionId createSession(CreateSessionCommand createSessionCommand) {
        var sessionUser = userPort
                .getById(createSessionCommand.owner())
                .orElseThrow(SessionInvalidUserException::new);

        // e.g sessionUser.canBeOwner()..

        var session = new Session(
                createSessionCommand.owner(),
                createSessionCommand.description(),
                createSessionCommand.startDate(),
                Duration.ofMinutes((long) createSessionCommand.durationInMinutes()),
                createSessionCommand.difficultyLevel(),
                createSessionCommand.visibility(),
                createSessionCommand.minPlayers(),
                createSessionCommand.maxPlayers()
        );

        var storedSession = sessionRepositoryPort.save(session);
        return storedSession.getId();
    }
}
