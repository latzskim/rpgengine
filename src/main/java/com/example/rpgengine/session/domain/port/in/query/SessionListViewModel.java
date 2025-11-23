package com.example.rpgengine.session.domain.port.in.query;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.port.out.read.SessionReadModel;

import java.time.LocalDateTime;

public record SessionListViewModel(
        String id,
        String title,
        String status,
        UserViewModel owner,
        String description,
        String difficulty,
        LocalDateTime startDate,
        Long durationInMinutes,
        Integer approvedPlayers,
        Integer pendingInvites,
        Integer minPlayers,
        Integer maxPlayers
) {
    public static SessionListViewModel from(SessionReadModel s) {
        return new SessionListViewModel(
                s.getId().getId().toString(),
                s.getTitle(),
                s.getStatus().toString(),
                UserViewModel.fromReadModel(s.getOwner()),
                s.getDescription(),
                s.getDifficulty().toString(),
                s.getStartDate(),
                s.getEstimatedDurationInMinutes(),
                s.getApprovedPlayers().split("#").length,
                s.getPendingInvites().split("#").length,
                Session.MIN_PLAYERS_EXCLUDING_GM,
                Session.MAX_PLAYERS_EXCLUDING_GM
        );
    }
}

