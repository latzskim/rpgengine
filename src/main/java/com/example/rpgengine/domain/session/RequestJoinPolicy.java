package com.example.rpgengine.domain.session;

import com.example.rpgengine.domain.session.valueobject.UserId;

public class RequestJoinPolicy implements JoinPolicy {
    @Override
    public void join(Session session, UserId userId) {
        session.joinRequest(userId);
    }
}
