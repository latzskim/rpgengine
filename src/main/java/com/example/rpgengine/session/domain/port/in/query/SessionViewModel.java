package com.example.rpgengine.session.domain.port.in.query;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModel;

import java.time.LocalDateTime;
import java.util.List;

public record SessionViewModel(
        String id,
        String title,
        String status,
        UserViewModel owner,
        String description,
        String difficulty,
        LocalDateTime startDate,
        Long durationInMinutes,
        List<UserViewModel> approvedPlayers,
        List<UserViewModel> pendingInvites,
        Integer minPlayers,
        Integer maxPlayers,
        Permissions permissions
) {

    public record Permissions(
            Boolean canJoin,
            Boolean isOwner
    ) {
    }

    public static SessionViewModel from(
            SessionReadModel session,
            List<UserViewModel> pendingInvites,
            List<UserViewModel> approvedPlayers,
            Permissions permissions
    ) {

        return new SessionViewModel(
                session.getId().getId().toString(),
                session.getTitle(),
                session.getStatus().toString(),
                UserViewModel.fromReadModel(session.getOwner()),
                session.getDescription(),
                session.getDifficulty().toString(),
                session.getStartDate(),
                session.getEstimatedDurationInMinutes(),
                approvedPlayers,
                pendingInvites,
                Session.MIN_PLAYERS_EXCLUDING_GM,
                Session.MAX_PLAYERS_EXCLUDING_GM,
                permissions
        );
    }
}
