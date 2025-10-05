package com.example.rpgengine.user.application;

import com.example.rpgengine.user.domain.port.in.AuthServicePort;
import com.example.rpgengine.user.domain.port.in.InvalidCredentialsException;
import com.example.rpgengine.user.domain.port.in.UserNotActiveException;
import com.example.rpgengine.user.domain.port.in.command.LoginCommand;
import com.example.rpgengine.user.domain.port.out.UserRepositoryPort;
import com.example.rpgengine.user.domain.valueobject.Email;
import com.example.rpgengine.config.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
class AuthService implements AuthServicePort {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepositoryPort userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
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
