package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.port.in.SessionServicePort;
import com.example.rpgengine.session.domain.port.in.command.CreateSessionCommand;
import com.example.rpgengine.session.domain.port.out.SessionRepositoryPort;
import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SessionServiceTest {
    @Autowired
    private SessionRepositoryPort sessionRepositoryPort;

    private UserPort userPort;

    private SessionServicePort sessionServicePort;

    @BeforeEach
    void setUp() {
        userPort = Mockito.mock(UserPort.class);
        sessionServicePort = new SessionService(sessionRepositoryPort, userPort);
    }

    @Test
    void shouldCreateASessionWithGameMaster() {
        // given:
        var userId = UserId.fromUUID(UUID.randomUUID());
        // Mock user as it is treated as a separate domain so maybe some external service or whatever
        Mockito.when(userPort.getById(userId)).thenReturn(Optional.of(new SessionUser(userId)));


        var cmd = new CreateSessionCommand(
                userId,
                "RPG Session in DB",
                LocalDateTime.now().plusDays(1),
                (short) 5,
                DifficultyLevel.HARD,
                Visibility.PUBLIC,
                1,
                6
        );

        // when:
        var sessionId = sessionServicePort.createSession(cmd);

        // then:
        assertThat(sessionId).isNotNull();
    }
}