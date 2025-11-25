package com.example.rpgengine.session.domain;

import com.example.rpgengine.session.domain.exception.InvalidInvitationCodeException;
import com.example.rpgengine.session.domain.valueobject.UserId;

public class AllowJoinPolicy implements JoinPolicy {
    private final String inviteCode;

    protected AllowJoinPolicy(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    @Override
    public void join(Session session, UserId userId) {
        if (!session.validateInviteCode(inviteCode)) {
            throw new InvalidInvitationCodeException();
        }
        session.addParticipant(userId);
    }
}
