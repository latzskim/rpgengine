package com.example.rpgengine.session.domain.event;

import com.example.rpgengine.session.domain.valueobject.SessionId;

import java.time.LocalDateTime;

public sealed interface SessionStatusEvent {
    record SessionHardDeleted(SessionId id) implements SessionStatusEvent {
    }

    record SessionCancelled(SessionId id, LocalDateTime when) implements SessionStatusEvent {
    }

    record SessionScheduled(SessionId id) implements SessionStatusEvent {
    }

    record SessionStarted(SessionId id, LocalDateTime when) implements SessionStatusEvent {
    }

    record SessionPaused(SessionId id) implements SessionStatusEvent {
    }

    record SessionResumed(SessionId id) implements SessionStatusEvent {
    }

    record SessionFinished(SessionId id, LocalDateTime when) implements SessionStatusEvent {
    }
}
