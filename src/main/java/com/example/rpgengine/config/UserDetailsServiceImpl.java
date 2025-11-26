package com.example.rpgengine.config;

import com.example.rpgengine.user.domain.User;
import com.example.rpgengine.user.domain.port.out.UserRepositoryPort;
import com.example.rpgengine.user.domain.valueobject.Email;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepositoryPort userRepository;

    public UserDetailsServiceImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new CustomUserDetails(
                user.getEmail().getValue(),
                user.getPasswordHash(),
                new ArrayList<>(),
                user.getDisplayName()
        );
    }
}