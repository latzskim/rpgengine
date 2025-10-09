package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.SessionParticipant;
import com.example.rpgengine.session.domain.port.in.SessionCommandServicePort;
import com.example.rpgengine.session.domain.port.in.command.CreateSessionCommand;
import com.example.rpgengine.session.domain.port.in.command.JoinSessionCommand;
import com.example.rpgengine.session.domain.port.out.SessionRepositoryPort;
import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Rollback(false)
class SessionServiceIntegrationTest {

    @Autowired
    private SessionRepositoryPort sessionRepositoryPort;


    @MockitoBean
    private UserPort userPort;

    private SessionCommandServicePort sessionCommandServicePort;

    @BeforeAll
    void setUp() {
        sessionRepositoryPort.deleteAll();
        sessionCommandServicePort = new SessionCommandService(sessionRepositoryPort, userPort);
    }

    @AfterAll
    void tearDown() {
    }

    // Shared state;
    private SessionId sessionId;

    @Test
    @Order(0)
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
        sessionId = sessionCommandServicePort.createSession(cmd);

        // then:
        assertThat(sessionId).isNotNull();
        assertSessionInDatabase(sessionId, cmd);
    }


    @Test
    @Order(1)
    public void shouldBeAbleToJoinToSessionWithInviteCode() {
        // given:
        var invitedUserId = UserId.fromUUID(UUID.randomUUID());
        assertThat(sessionId).isNotNull();

        var session = sessionRepositoryPort.findById(sessionId).orElse(null);
        assertThat(session).isNotNull();

        // when:
        sessionCommandServicePort.join(new JoinSessionCommand(
                sessionId,
                invitedUserId,
                session.getInviteCode()
        ));

        // then:
        var updatedSession = sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new AssertionError("Session not found in database"));

        assertThat(updatedSession.getParticipants()).hasSize(2);
        var invitedUser = updatedSession
                .getParticipants()
                .stream()
                .filter(p -> p.getUserId().equals(invitedUserId))
                .findFirst().orElseThrow(() -> new AssertionError("User not found in database"));

        assertThat(invitedUser.getUserId()).isEqualTo(invitedUserId);
        assertThat(invitedUser.getRole()).isEqualTo(ParticipantRole.PLAYER);
        assertThat(invitedUser.getCharacterId()).isNull();
    }

    @Test
    @Order(3)
    public void shouldBeAbleToRequestToJoinSession() {
        // given:
        final String EMPTY_CODE = "";

        var requestingUserId = UserId.fromUUID(UUID.randomUUID());
        assertThat(sessionId).isNotNull();

        // when:
        sessionCommandServicePort.join(new JoinSessionCommand(
                sessionId,
                requestingUserId,
                EMPTY_CODE
        ));

        // then:
        var updatedSession = sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new AssertionError("Session not found in database"));

        assertThat(updatedSession.getJoinRequests()).hasSize(1);
        var joinToSessionRequest = updatedSession
                .getJoinRequests()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Join request not found in database"));

        assertThat(joinToSessionRequest.getUserId()).isEqualTo(requestingUserId);
    }


    private void assertSessionInDatabase(SessionId sessionId, CreateSessionCommand cmd) {
        var session = sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new AssertionError("Session not found in database"));

        assertThat(session).isNotNull();
        assertThat(session.getId()).isEqualTo(sessionId);
        assertThat(session.getDescription()).isEqualTo(cmd.description());
        assertThat(session.getEstimatedDurationInMinutes()).isEqualTo((long) cmd.durationInMinutes());
        assertThat(session.getDifficulty()).isEqualTo(cmd.difficultyLevel());
        assertThat(session.getMinPlayers()).isEqualTo(cmd.minPlayers());
        assertThat(session.getMaxPlayers()).isEqualTo(cmd.maxPlayers());
        assertThat(session.getVisibility()).isEqualTo(cmd.visibility());
        assertThat(session.getStartDate()).isNotNull();
        assertThat(session.getInviteCode()).isNotNull();

        Optional<SessionParticipant> gmParticipant = session.getParticipants().stream()
                .filter(p -> p.getUserId().equals(cmd.owner()))
                .findFirst();
        assertThat(gmParticipant).isPresent();
        assertThat(gmParticipant.get().getRole()).isEqualTo(ParticipantRole.GAMEMASTER);
        assertThat(gmParticipant.get().getCharacterId()).isNull();

        var joinRequestCount = session.getJoinRequests().size();
        assertThat(joinRequestCount).isEqualTo(0);
    }
}