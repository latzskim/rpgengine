package com.example.rpgengine.session.domain.port.in;

import com.example.rpgengine.session.domain.port.in.command.CreateSessionCommand;
import com.example.rpgengine.session.domain.port.in.command.JoinSessionCommand;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.UserId;

public interface SessionCommandServicePort {
    SessionId createSession(CreateSessionCommand createSessionCommand);

    void join(JoinSessionCommand joinSessionCommand);
}
