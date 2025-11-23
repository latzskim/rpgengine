package com.example.rpgengine.session.domain.port.in.query;

import com.example.rpgengine.session.domain.port.out.read.UserReadModel;

public record UserViewModel(
        String id,
        String displayName,
        String avatarUrl
) {
    public static UserViewModel fromReadModel(UserReadModel s) {
        return new UserViewModel(
                s.getId().getUserId().toString(),
                s.getDisplayName(),
                s.getAvatarUrl()
        );
    }
}
