package com.example.rpgengine.session.domain.port.out.read;

import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import com.example.rpgengine.session.domain.valueobject.UserId;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

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
        return (root, cq, cb) -> {
            Path<String> approvedInvites = root.get("approvedPlayers");
            Expression<Integer> totalLength = cb.length(approvedInvites);
            Expression<String> replacedString = cb.function(
                    "REPLACE",
                    String.class,
                    approvedInvites,
                    cb.literal("#"),
                    cb.literal("")
            );

            Expression<Integer> lengthWithoutHashes = cb.length(replacedString);
            Expression<Integer> hashCount = cb.diff(totalLength, lengthWithoutHashes);
            Expression<Integer> safeCount = cb.coalesce(hashCount, 0);

            return cb.lessThan(safeCount, maxPlayers);
        };
    }
}
