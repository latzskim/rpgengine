package com.example.rpgengine.session.domain.port.in;

import com.example.rpgengine.session.domain.port.in.command.CreateSessionCommand;
import com.example.rpgengine.session.domain.valueobject.SessionId;

public interface SessionCommandServicePort {
    SessionId createSession(CreateSessionCommand createSessionCommand);
}
