package com.example.rpgengine.user.application;

import com.example.rpgengine.user.domain.port.in.RegistrationServicePort;
import com.example.rpgengine.user.domain.port.in.command.ActivateCommand;
import com.example.rpgengine.user.domain.port.in.command.RegisterCommand;
import com.example.rpgengine.user.domain.User;
import com.example.rpgengine.user.domain.port.out.UserRepositoryPort;
import com.example.rpgengine.user.domain.valueobject.Email;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
class RegistrationService implements RegistrationServicePort {
    private final UserRepositoryPort userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepositoryPort userRepository,
            ApplicationEventPublisher eventPublisher,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterCommand command) {
        var email = new Email(command.email());

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalStateException("User already exists");
        }

        var hashedPassword = passwordEncoder.encode(command.password());
        var user = new User(email, hashedPassword);

        userRepository.save(user);

        user.getDomainEvents().forEach(eventPublisher::publishEvent);
    }

    public void activateUser(ActivateCommand command) {
        var user = userRepository
                .findByActivationToken(command.activationToken())
                .orElseThrow(() -> new IllegalStateException("Invalid activation token"));


        user.activate();
        userRepository.save(user);

        user.getDomainEvents().forEach(eventPublisher::publishEvent);
    }
}
