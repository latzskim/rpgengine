package com.example.rpgengine.application.profile;

import com.example.rpgengine.application.profile.query.ProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    public ProfileResponse myProfile(String username) {
        return new ProfileResponse(username);
    }
}
