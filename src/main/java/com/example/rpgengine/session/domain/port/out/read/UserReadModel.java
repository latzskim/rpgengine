package com.example.rpgengine.session.domain.port.out.read;


import com.example.rpgengine.session.domain.valueobject.UserId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.Immutable;


@Entity
@Table(name = "user_read_model")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@EqualsAndHashCode
public class UserReadModel {
    @EmbeddedId
    private UserId id;

    @Column(name = "display_name", unique = true, nullable = false)
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}