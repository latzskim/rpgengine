package com.example.rpgengine.session.domain.event;

import com.example.rpgengine.session.domain.valueobject.SessionId;

import java.time.LocalDateTime;

public sealed interface SessionStateEvent {
    record SessionHardDeleted(SessionId id) implements SessionStateEvent {
    }

    record SessionCancelled(SessionId id, LocalDateTime when) implements SessionStateEvent {
    }

    record SessionScheduled(SessionId id) implements SessionStateEvent {
    }

    record SessionStarted(SessionId id, LocalDateTime when) implements SessionStateEvent {
    }

    record SessionPaused(SessionId id) implements SessionStateEvent {
    }

    record SessionResumed(SessionId id) implements SessionStateEvent {
    }

    record SessionFinished(SessionId id, LocalDateTime when) implements SessionStateEvent {
    }
}
