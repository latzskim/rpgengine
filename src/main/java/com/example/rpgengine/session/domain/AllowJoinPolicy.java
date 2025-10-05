package com.example.rpgengine.session.domain;

import com.example.rpgengine.session.domain.valueobject.UserId;

public class AllowJoinPolicy implements JoinPolicy {
    @Override
    public void join(Session session, UserId userId) {
        session.addParticipant(userId);
    }
}
