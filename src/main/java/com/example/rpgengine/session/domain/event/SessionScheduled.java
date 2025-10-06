package com.example.rpgengine.session.domain.event;

import com.example.rpgengine.session.domain.valueobject.SessionId;

public record SessionScheduled(SessionId sessionId) {
}
