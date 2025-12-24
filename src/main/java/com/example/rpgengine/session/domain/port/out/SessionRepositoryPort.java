package com.example.rpgengine.session.domain.port.out;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;

import java.time.LocalDateTime;


public interface SessionRepositoryPort extends JpaRepository<Session, SessionId> {
    Streamable<Session> findByStartDateLessThanEqualAndStatus(LocalDateTime startDateBy, SessionStatus status);
}
