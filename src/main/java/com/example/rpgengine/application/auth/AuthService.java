package com.example.rpgengine.application.auth;

import com.example.rpgengine.application.auth.command.LoginCommand;
import com.example.rpgengine.domain.user.repository.UserRepository;
import com.example.rpgengine.domain.user.valueobject.Email;
import com.example.rpgengine.infrastructure.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String authenticateUser(LoginCommand loginCommand) {
        var user = userRepository
                .findByEmail(new Email(loginCommand.email()))
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(loginCommand.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new UserNotActiveException();
        }

        String jwt = jwtService.generateJwtToken(user.getEmail().getValue());
        return format("Bearer %s", jwt);
    }
}
