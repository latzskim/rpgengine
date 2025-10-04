package com.example.rpgengine.domain.session.event;

import com.example.rpgengine.domain.session.valueobject.SessionId;
import com.example.rpgengine.domain.session.valueobject.UserId;

public record SessionGMAssigned(SessionId sessionId, UserId gmId) {
}
