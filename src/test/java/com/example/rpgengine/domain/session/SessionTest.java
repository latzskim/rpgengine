package com.example.rpgengine.domain.session;

import com.example.rpgengine.domain.session.event.SessionGMAssigned;
import com.example.rpgengine.domain.session.event.SessionUserJoined;
import com.example.rpgengine.domain.session.exception.InvalidInvitationCodeException;
import com.example.rpgengine.domain.session.exception.SessionGameMasterAlreadyAssignedException;
import com.example.rpgengine.domain.session.valueobject.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SessionTest {
    final int validMinPlayers = 1;
    final int validMaxPlayers = 10;

    @Test
    public void shouldOwnerBeAssignedAsADefaultGameMaster() {
        // given & when:
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

    @Test
    public void shouldAllowUserWithInvitationCodeToJoinAsPlayerToPublicOrPrivateSession() {
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
        assertThat(session.getParticipants()).hasSize(1); // GM

        var invitationCode = session.getInviteCode();
        var userId = UserId.fromUUID(UUID.randomUUID());

        // when:
        session.joinAsPlayer(userId, invitationCode);

        // then:
        assertThat(session.getParticipants()).hasSize(2); // GM + Player
        var optParticipant = findParticipant(userId, session.getParticipants());
        assertThat(optParticipant.isPresent()).isTrue();

        var participant = optParticipant.get();
        assertThat(participant.getRole()).isEqualTo(ParticipantRole.PLAYER);


        assertThat(session.getDomainEvents()).hasSize(1);
        var event = session.getDomainEvents().getFirst();
        assertThat(event).isInstanceOf(SessionUserJoined.class);
        SessionUserJoined userJoinedEvent = (SessionUserJoined) event;
        assertThat(userJoinedEvent).isEqualTo(new SessionUserJoined(session.getId(), userId));
    }

    @Test
    public void shouldRejectUserWithInvalidInvitationCode() {
        // given:
        var session = new Session();
        var userId = UserId.fromUUID(UUID.randomUUID());
        var inviteCode = "invalidCode";

        // when & then:
        assertThrows(InvalidInvitationCodeException.class, () -> {
            session.joinAsPlayer(userId, inviteCode);
        });
    }

    private Optional<SessionParticipant> findParticipant(UserId userId, Set<SessionParticipant> participants) {
        return participants
                .stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst();
    }
}