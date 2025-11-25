package com.example.rpgengine.session.domain.port.in;

import com.example.rpgengine.session.domain.port.in.command.*;
import com.example.rpgengine.session.domain.valueobject.SessionId;

public interface SessionCommandServicePort {
    SessionId createSession(CreateSessionCommand createSessionCommand);

    void join(JoinSessionCommand joinSessionCommand);

    void handleUserJoinRequest(HandleUserJoinSessionDecisionCommand handleUserJoinSessionDecisionCommand);

    void updateSession(UpdateSessionCommand updateSessionCommand);

    void scheduleSession(ScheduleSessionCommand scheduleSessionCommand);

    void deleteSession(DeleteSessionCommand deleteSessionCommand);
}
