package com.example.rpgengine.domain.session;

import com.example.rpgengine.domain.session.valueobject.UserId;

public interface JoinPolicy {
    /***
     *
     * @param session to which player wants to join.
     * @param userId is a player identifier who wants to join to session.
     */
    void join(Session session, UserId userId);
}
