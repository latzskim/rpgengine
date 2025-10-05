package com.example.rpgengine.session.domain;

import com.example.rpgengine.session.domain.valueobject.UserId;

public interface JoinPolicy {
    /***
     *
     * @param session to which player wants to join.
     * @param userId is a player identifier who wants to join to session.
     */
    void join(Session session, UserId userId);
}
