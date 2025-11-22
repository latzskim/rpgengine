package com.example.rpgengine.session.domain.port.out.read;

import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface SessionReadModelRepositoryPort
        extends JpaRepository<SessionReadModel, SessionId>, JpaSpecificationExecutor<SessionReadModel> {
    List<SessionReadModel> findByOwnerId(UserId userId);
}
