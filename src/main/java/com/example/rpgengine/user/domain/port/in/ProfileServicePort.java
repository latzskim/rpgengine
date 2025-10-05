package com.example.rpgengine.user.domain.port.in;

import com.example.rpgengine.user.domain.port.in.query.ProfileResponse;

public interface ProfileServicePort {
    ProfileResponse myProfile(String username);
}
