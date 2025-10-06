package com.example.rpgengine.session.domain;

import com.example.rpgengine.session.domain.event.SessionGMAssigned;
import com.example.rpgengine.session.domain.event.SessionScheduled;
import com.example.rpgengine.session.domain.event.SessionUserJoinRequested;
import com.example.rpgengine.session.domain.event.SessionUserJoined;
import com.example.rpgengine.session.domain.exception.InvalidInvitationCodeException;
import com.example.rpgengine.session.domain.exception.SessionGameMasterAlreadyAssignedException;
import com.example.rpgengine.session.domain.exception.SessionPrivateException;
import com.example.rpgengine.session.domain.exception.SessionScheduleException;
import com.example.rpgengine.session.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;


class SessionTest {
    final static int validMinPlayers = 1;
    final static int validMaxPlayers = 10;

    @Test
    public void shouldOwnerBeAssignedAsADefaultGameMaster() {
        // given & when:
        var session = makePublicSession();

        // then:
        assertThat(session.getParticipants()).hasSize(1);
        var participant = session.getParticipants().iterator().next();
        assertThat(participant.getUserId()).isEqualTo(session.getOwnerId());
        assertThat(participant.getRole()).isEqualTo(ParticipantRole.GAMEMASTER);
        assertThat(participant.getCharacterId()).isNull();
    }

    @Test
    public void shouldAssignGameMasterIfThereIsNoGameMasterAssignedAlready() {
        // given:
        var gmId = UserId.fromUUID(UUID.randomUUID());
        var session = new Session();
        assertThat(session.getParticipants()).isEmpty();
        assertThat(session.getDomainEvents()).isEmpty();

        // when:
        session.assignGameMaster(gmId);

        // then:
        assertThat(session.getParticipants()).hasSize(1);
        var participant = session.getParticipants().iterator().next();

        assertThat(participant.getUserId()).isEqualTo(gmId);
        assertThat(participant.getRole()).isEqualTo(ParticipantRole.GAMEMASTER);
        assertThat(participant.getCharacterId()).isNull();

        assertThat(session.getDomainEvents()).hasSize(1);
        var event = session.getDomainEvents().getFirst();
        assertThat(event).isInstanceOf(SessionGMAssigned.class);
        SessionGMAssigned gm = (SessionGMAssigned) event;
        assertThat(gm).isEqualTo(new SessionGMAssigned(session.getId(), gmId));
    }

    @Test
    public void shouldThrowSessionGameMasterAlreadyAssignedExceptionWhenGameMasterIsAlreadyAssigned() {
        // given:
        var gmId = UserId.fromUUID(UUID.randomUUID());
        var session = new Session();
        session.assignGameMaster(gmId);

        assertThat(session.getParticipants()).hasSize(1);
        var participant = session.getParticipants().iterator().next();
        assertThat(participant.getRole()).isEqualTo(ParticipantRole.GAMEMASTER);

        var otherGmId = UserId.fromUUID(UUID.randomUUID());

        // when & then:
        assertThrows(SessionGameMasterAlreadyAssignedException.class, () -> {
            session.assignGameMaster(otherGmId);
        });
    }

    @ParameterizedTest
    @MethodSource("provideJoinToPublicSession")
    public void shouldJoinToPublicSession(String inviteCode, Class<?> clazz) {
        // given:
        var session = makePublicSession();

        var givenInviteCode = switch (inviteCode) {
            case "validCode" -> session.getInviteCode();
            case "invalidCode" -> session.getInviteCode() + "invalid";
            default -> "";
        };

        var invitePolicy = JoinSessionPolicyFactory.createJoinPolicy(givenInviteCode);
        var userId = UserId.fromUUID(UUID.randomUUID());
        assertThat(session.getParticipants()).hasSize(1); // GM
        assertThat(session.getJoinRequests()).hasSize(0);

        // when:
        session.join(userId, invitePolicy);

        // then:
        assertThat(session.getDomainEvents()).hasSize(1);
        var event = session.getDomainEvents().getFirst();
        assertThat(event).isInstanceOf(clazz);

        switch (event) {
            case SessionUserJoined d -> {
                assertThat(d)
                        .isEqualTo(new SessionUserJoined(session.getId(), userId));

                assertThat(session.getParticipants()).hasSize(2); // GM + player
            }
            case SessionUserJoinRequested d -> {
                assertThat(d)
                        .isEqualTo(new SessionUserJoinRequested(session.getId(), userId));

                assertThat(session.getJoinRequests()).hasSize(1);
            }
            default -> fail("event should be eiter SessionUserJoined or SessionUserJoinRequested");
        }
    }

    @Test
    public void shouldThrowSessionPrivateExceptionWhenNoInviteCode() {
        // given:
        var session = makePrivateSession();

        var emptyInviteCode = "";
        var userId = UserId.fromUUID(UUID.randomUUID());

        // when & then:
        assertThrows(SessionPrivateException.class, () -> {
            session.join(userId, JoinSessionPolicyFactory.createJoinPolicy(emptyInviteCode));
        });
    }


    @Test
    public void shouldThrowInvalidInvitationCodeExceptionWhenInviteCodeIsInvalid() {
        // given:
        var session = new Session(
                UserId.fromUUID(UUID.randomUUID()),
                "Lotr Session",
                LocalDateTime.now().plusDays(1),
                Duration.ofHours(5),
                DifficultyLevel.EASY,
                Visibility.PUBLIC,
                validMinPlayers,
                validMaxPlayers
        );

        var userId = UserId.fromUUID(UUID.randomUUID());

        // when & then
        assertThrows(InvalidInvitationCodeException.class, () -> {
            session.join(userId, JoinSessionPolicyFactory.createJoinPolicy("invalidCode"));
        });
    }


    @Test
    public void shouldScheduleDraftSession() {
        // given:
        var session = makePublicSession();
        assertThat(session.getDomainEvents()).hasSize(0);

        // when:
        session.scheduleSession();

        // then:
        assertThat(session.getDomainEvents()).hasSize(1);
        var event = session.getDomainEvents().getFirst();
        assertThat(event).isInstanceOf(SessionScheduled.class);
        SessionScheduled sessionScheduled = (SessionScheduled) event;
        assertThat(sessionScheduled.sessionId()).isEqualTo(session.getId());
    }

    //    @ParameterizedTest
//    @EnumSource(value = SessionStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
    @Test
    public void shouldThrowSessionScheduleException() {
        // given:
        var session = new Session();
        session.scheduleSession(); // make it already scheduled

        // when & then:
        assertThrows(SessionScheduleException.class, session::scheduleSession);
    }


    private static Stream<Arguments> provideJoinToPublicSession() {
        return Stream.of(
                Arguments.of("", SessionUserJoinRequested.class),
                Arguments.of("validCode", SessionUserJoined.class)
        );
    }

    private static Session makePrivateSession() {
        return new Session(
                UserId.fromUUID(UUID.randomUUID()),
                "Lotr Session",
                LocalDateTime.now().plusDays(1),
                Duration.ofHours(5),
                DifficultyLevel.EASY,
                Visibility.PRIVATE,
                validMinPlayers,
                validMaxPlayers
        );
    }

    private static Session makePublicSession() {
        return new Session(
                UserId.fromUUID(UUID.randomUUID()),
                "Lotr Session",
                LocalDateTime.now().plusDays(1),
                Duration.ofHours(5),
                DifficultyLevel.EASY,
                Visibility.PUBLIC,
                validMinPlayers,
                validMaxPlayers
        );
    }
}