package com.example.rpgengine.session.domain.event;

import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.UserId;

public record SessionUserJoined(SessionId sessionId, UserId gmId) {
}
