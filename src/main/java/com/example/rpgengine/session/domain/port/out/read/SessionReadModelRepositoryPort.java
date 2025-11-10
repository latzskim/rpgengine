package com.example.rpgengine.session.domain.port.out.read;

import com.example.rpgengine.session.domain.valueobject.SessionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SessionReadModelRepositoryPort
        extends JpaRepository<SessionReadModel, SessionId>, JpaSpecificationExecutor<SessionReadModel> {

}
