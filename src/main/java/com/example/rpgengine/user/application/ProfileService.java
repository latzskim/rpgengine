package com.example.rpgengine.user.application;

import com.example.rpgengine.user.domain.port.in.ProfileServicePort;
import com.example.rpgengine.user.domain.port.in.query.ProfileResponse;
import org.springframework.stereotype.Service;

@Service
class ProfileService implements ProfileServicePort {
    public ProfileResponse myProfile(String username) {
        return new ProfileResponse(username);
    }
}
