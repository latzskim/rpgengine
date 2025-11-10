package com.example.rpgengine.session.domain.port.in.query;

import com.example.rpgengine.session.domain.port.out.read.SessionReadModel;

public record SessionListViewModel(
        String id,
        String title,
        String status,
        String ownerId,
        String displayName,
        String ownerAvatarUrl,
        String description,
        Integer approvedPlayers,
        Integer pendingInvites
) {
    public static SessionListViewModel from(SessionReadModel s) {
        return new SessionListViewModel(
                s.getId().getId().toString(),
                s.getTitle(),
                s.getStatus().toString(),
                s.getOwnerId().toString(),
                s.getOwner().getDisplayName(),
                s.getOwner().getAvatarUrl(),
                s.getDescription(),
                s.getApprovedPlayers(),
                s.getPendingInvites()
        );
    }
}

