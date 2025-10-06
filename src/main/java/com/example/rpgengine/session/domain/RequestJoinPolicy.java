package com.example.rpgengine.session.domain;

import com.example.rpgengine.session.domain.exception.SessionPrivateException;
import com.example.rpgengine.session.domain.valueobject.UserId;

public class RequestJoinPolicy implements JoinPolicy {
    @Override
    public void join(Session session, UserId userId) {
        if (session.isPrivate()) {
            throw new SessionPrivateException();
        }
        session.joinRequest(userId);
    }
}
