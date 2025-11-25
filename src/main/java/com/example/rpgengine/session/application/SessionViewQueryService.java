package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.exception.SessionForbiddenException;
import com.example.rpgengine.session.domain.exception.SessionNotFoundException;
import com.example.rpgengine.session.domain.port.in.query.*;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModel;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModelRepositoryPort;
import com.example.rpgengine.session.domain.port.in.SessionViewQueryServicePort;
import com.example.rpgengine.session.domain.port.out.read.SessionSpecifications;
import com.example.rpgengine.session.domain.port.out.read.SessionUserReadModelRepositoryPort;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import com.example.rpgengine.session.domain.valueobject.UserId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Service
class SessionViewQueryService implements SessionViewQueryServicePort {
    private final SessionReadModelRepositoryPort sessionReadModelRepositoryPort;

    SessionViewQueryService(SessionReadModelRepositoryPort sessionReadModelRepositoryPort) {
        this.sessionReadModelRepositoryPort = sessionReadModelRepositoryPort;
    }

    @Override
    public List<SessionListViewModel> getSessionsByUserId(UserId userId) {
        return sessionReadModelRepositoryPort.findByOwnerId(userId).stream()
                .map(SessionListViewModel::from)
                .toList();
    }

    @Override
    public List<SessionListViewModel> getSessionsEligibleToPlay(SessionsEligibleToPlayQuery query) {
        if (query.byStatus() == null) {
            throw new IllegalArgumentException("filter: status must be specified");
        }

        var eligibleStatuses = List.of(
                SessionStatus.SCHEDULED,
                SessionStatus.IN_PROGRESS,
                SessionStatus.PAUSED
        );

        if (!eligibleStatuses.contains(query.byStatus())) {
            throw new IllegalArgumentException(format("filter: status \"%s\" is not eligible to play", query.byStatus()));
        }

        var q = buildCustomSpecifications(query);
        var p = buildPage(query.page(), query.elements(), query.sortBy());

        return sessionReadModelRepositoryPort.findAll(q, p).stream()
                .map(SessionListViewModel::from)
                .toList();
    }

    @Override
    public SessionViewModel getSessionByUserId(SessionId sessionId, UserId userId) {
        var session = sessionReadModelRepositoryPort.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);

        if (session.getStatus().equals(SessionStatus.DRAFT) && !session.getOwnerId().equals(userId)) {
            throw new SessionForbiddenException("Only owners can edit draft sessions");
        }

        List<UserViewModel> pendingInvites = session.getPendingInvites().stream()
                .map(UserViewModel::fromReadModel)
                .toList();
        List<UserViewModel> approvedPlayers = session.getApprovedPlayers().stream()
                .map(UserViewModel::fromReadModel)
                .toList();

        return SessionViewModel.from(
                session,
                pendingInvites,
                approvedPlayers,
                new SessionViewModel.Permissions(
                        canJoin(userId, session),
                        session.getOwnerId().equals(userId),
                        session.getOwnerId().equals(userId) && session.getStatus().equals(SessionStatus.DRAFT)
                )
        );
    }

    private static Boolean canJoin(UserId userId, SessionReadModel session) {
        boolean isPending = session.getPendingInvites().stream()
                .anyMatch(u -> u.getId().equals(userId));
        boolean isApproved = session.getApprovedPlayers().stream()
                .anyMatch(u -> u.getId().equals(userId));

        return !isPending && !isApproved;
    }



    private static PageRequest buildPage(int page, int elements, SortBy sortBy) {
        Sort s = switch (sortBy.sortDirection()) {
            case ASC -> Sort.by(Sort.Direction.ASC, sortBy.sortField().toString());
            case DESC -> Sort.by(Sort.Direction.DESC, sortBy.sortField().toString());
        };

        return PageRequest.of(page, elements, s);
    }

    private static Specification<@NotNull SessionReadModel> buildCustomSpecifications(SessionsEligibleToPlayQuery query) {
        var p = SessionSpecifications.bySessionStatus(query.byStatus());
        p = p.and(SessionSpecifications.lessApprovedPlayersThan(Session.MAX_PLAYERS_EXCLUDING_GM));

        if (query.byUserId() != null) {
            p = p.and(SessionSpecifications.byUserId(query.byUserId()));
        }

        return p;
    }
}
