package com.example.rpgengine.controller.profile;


import com.example.rpgengine.application.profile.ProfileService;
import com.example.rpgengine.application.profile.query.ProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/me")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> myProfile() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        var principal = auth.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            var myProfile = profileService.myProfile(userDetails.getUsername());
            return ResponseEntity.ok(myProfile);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
