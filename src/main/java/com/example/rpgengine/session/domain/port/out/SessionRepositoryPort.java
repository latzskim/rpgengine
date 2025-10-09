package com.example.rpgengine.session.domain.port.out;

import com.example.rpgengine.session.domain.Session;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SessionRepositoryPort extends JpaRepository<Session, SessionId> {
}
