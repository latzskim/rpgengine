package com.example.rpgengine.session.application;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.port.in.query.SessionListViewModel;
import com.example.rpgengine.session.domain.port.in.query.SortBy;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModel;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModelRepositoryPort;
import com.example.rpgengine.session.domain.port.in.SessionViewQueryServicePort;
import com.example.rpgengine.session.domain.port.in.query.SessionsEligibleToPlayQuery;
import com.example.rpgengine.session.domain.port.out.read.SessionSpecifications;
import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import com.example.rpgengine.session.domain.valueobject.UserId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;

@Service
class SessionViewQueryService implements SessionViewQueryServicePort {
    private final SessionReadModelRepositoryPort sessionReadModelRepositoryPort;

    SessionViewQueryService(SessionReadModelRepositoryPort sessionReadModelRepositoryPort) {
        this.sessionReadModelRepositoryPort = sessionReadModelRepositoryPort;
    }

    @Override
    public List<SessionListViewModel> getSessionsByUserId(UserId userId) {
        return List.of();
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
