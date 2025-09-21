package com.example.rpgengine.infrastructure.config;

import com.example.rpgengine.domain.user.User;
import com.example.rpgengine.domain.user.repository.UserRepository;
import com.example.rpgengine.domain.user.valueobject.Email;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(new Email(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail().getValue())
                .password(user.getPasswordHash())
                .authorities(new ArrayList<>())
                .build();
    }
}