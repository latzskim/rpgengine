package com.example.rpgengine.domain.session;

import com.example.rpgengine.domain.session.valueobject.UserId;

public class AllowJoinPolicy implements JoinPolicy {
    @Override
    public void join(Session session, UserId userId) {
        session.addParticipant(userId);
    }
}
