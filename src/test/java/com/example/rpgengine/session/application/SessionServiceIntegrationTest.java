package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.port.in.SessionCommandServicePort;
import com.example.rpgengine.session.domain.port.in.command.CreateSessionCommand;
import com.example.rpgengine.session.domain.port.out.SessionRepositoryPort;
import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SessionServiceIntegrationTest {

    @Autowired
    private SessionRepositoryPort sessionRepositoryPort;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private UserPort userPort;

    private SessionCommandServicePort sessionCommandServicePort;

    @BeforeEach
    void setUp() {
        sessionCommandServicePort = new SessionCommandService(sessionRepositoryPort, userPort);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("DELETE FROM SESSION_PARTICIPANTS");
        jdbcTemplate.execute("DELETE FROM JOIN_REQUESTS");
        jdbcTemplate.execute("DELETE FROM SESSIONS");
    }

    @Test
    void shouldCreateASessionWithGameMaster() {
        // given:
        var userId = UserId.fromUUID(UUID.randomUUID());
        // Mock user as it is treated as a separate domain so maybe some external service or whatever
        Mockito
                .when(userPort.getById(userId))
                .thenReturn(Optional.of(new SessionUser(userId)));


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
        var sessionId = sessionCommandServicePort.createSession(cmd);

        // then:
        assertThat(sessionId).isNotNull();
        assertSessionInDatabase(sessionId, cmd);
    }

    private void assertSessionInDatabase(SessionId sessionId, CreateSessionCommand cmd) {
        var sessionMap = jdbcTemplate.queryForMap(
                "SELECT * FROM SESSIONS WHERE ID = ?",
                sessionId.getId()
        );

        assertThat(sessionMap)
                .isNotNull()
                .containsEntry("id", sessionId.getId())
                .containsEntry("description", cmd.description())
                .containsEntry("estimated_duration", (long) cmd.durationInMinutes())
                .containsEntry("difficulty", cmd.difficultyLevel().toString())
                .containsEntry("min_players", cmd.minPlayers())
                .containsEntry("max_players", cmd.maxPlayers())
                .containsEntry("visibility", cmd.visibility().toString())
                .containsKey("start_date")
                .containsKey("invite_code");


        var sessionParticipants = jdbcTemplate.queryForMap(
                "SELECT * FROM SESSION_PARTICIPANTS WHERE SESSION_ID = ?",
                sessionId.getId()
        );

        assertThat(sessionParticipants)
                .isNotNull()
                .containsEntry("session_id", sessionId.getId())
                .containsEntry("user_id", cmd.owner().getUserId())
                .containsEntry("role", ParticipantRole.GAMEMASTER.toString())
                .containsEntry("character_id", null);


        var joinRequestCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM JOIN_REQUESTS WHERE SESSION_ID = ?",
                Long.class,
                sessionId.getId()
        );
        assertThat(joinRequestCount).isEqualTo(0L);
    }
}