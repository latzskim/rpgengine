package com.example.rpgengine.session.domain.port.in.command;

import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.UserId;

public record HandleUserJoinSessionDecisionCommand(
        UserId ownerId,
        SessionId sessionId,
        UserId userId,
        boolean shouldApproveRequest
) {
}
