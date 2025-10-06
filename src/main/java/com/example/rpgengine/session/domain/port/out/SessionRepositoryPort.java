package com.example.rpgengine.session.domain.port.out;

import com.example.rpgengine.session.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SessionRepositoryPort extends JpaRepository<Session, UUID> {
}
