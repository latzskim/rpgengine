package com.example.rpgengine.session.domain.port.in;

import com.example.rpgengine.session.domain.port.in.query.SessionListViewModel;
import com.example.rpgengine.session.domain.port.in.query.SessionsEligibleToPlayQuery;
import com.example.rpgengine.session.domain.valueobject.UserId;

import java.util.List;

public interface SessionViewQueryServicePort {
    List<SessionListViewModel> getSessionsByUserId(UserId userId);

    List<SessionListViewModel> getSessionsEligibleToPlay(SessionsEligibleToPlayQuery query);
}
