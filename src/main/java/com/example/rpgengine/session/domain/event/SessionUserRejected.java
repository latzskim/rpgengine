package com.example.rpgengine.session.domain.event;

import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.UserId;

public record SessionUserRejected(SessionId sessionId, UserId userId) {
}
