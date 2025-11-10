package com.example.rpgengine.session.domain.port.in.query;


import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import com.example.rpgengine.session.domain.valueobject.UserId;

public record SessionsEligibleToPlayQuery(
        SessionStatus byStatus,
        UserId byUserId,
        Integer page,
        Integer elements,
        SortBy sortBy
) {
}
