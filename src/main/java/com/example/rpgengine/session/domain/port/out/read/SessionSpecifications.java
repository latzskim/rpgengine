package com.example.rpgengine.session.domain.port.out.read;

import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import com.example.rpgengine.session.domain.valueobject.UserId;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

public class SessionSpecifications {
    public static Specification<@NotNull SessionReadModel> byUserId(UserId userId) {
        return (root, cq, cb) ->
                cb.equal(root.get("ownerId"), userId);
    }

    public static Specification<@NotNull SessionReadModel> bySessionStatus(SessionStatus sessionStatus) {
        return (root, cq, cb) ->
                cb.equal(root.get("status"), sessionStatus);
    }

    public static Specification<@NotNull SessionReadModel> lessApprovedPlayersThan(Integer maxPlayers) {
        return (root, cq, cb) ->
                cb.lessThan(root.get("approvedPlayers"), maxPlayers);
    }
}
