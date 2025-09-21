package com.example.rpgengine.application.registration;

import com.example.rpgengine.application.registration.command.ActivateCommand;
import com.example.rpgengine.application.registration.command.RegisterCommand;
import com.example.rpgengine.domain.user.User;
import com.example.rpgengine.domain.user.repository.UserRepository;
import com.example.rpgengine.domain.user.valueobject.Email;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(
            UserRepository userRepository,
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

        for (var event : user.getDomainEvents()) {
            eventPublisher.publishEvent(event);
        }
    }

    public void activateUser(ActivateCommand command) {
        var user = userRepository
                .findByActivationToken(command.activationToken())
                .orElseThrow(() -> new IllegalStateException("Invalid activation token"));


        user.activate();
        userRepository.save(user);

        for (var event : user.getDomainEvents()) {
            eventPublisher.publishEvent(event);
        }
    }
}
