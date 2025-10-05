package com.example.rpgengine.session.domain;

import com.example.rpgengine.session.domain.valueobject.UserId;

public class RequestJoinPolicy implements JoinPolicy {
    @Override
    public void join(Session session, UserId userId) {
        session.joinRequest(userId);
    }
}
