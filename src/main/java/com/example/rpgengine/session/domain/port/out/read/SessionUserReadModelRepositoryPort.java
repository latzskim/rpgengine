package com.example.rpgengine.session.domain.port.out.read;

import com.example.rpgengine.session.domain.valueobject.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionUserReadModelRepositoryPort extends JpaRepository<UserReadModel, UserId> {
}
