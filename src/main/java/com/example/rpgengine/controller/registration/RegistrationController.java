package com.example.rpgengine.controller.registration;


import com.example.rpgengine.application.registration.RegistrationService;
import com.example.rpgengine.application.registration.command.ActivateCommand;
import com.example.rpgengine.application.registration.command.RegisterCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final RegistrationService userRegisterService;

    RegistrationController(RegistrationService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterCommand command) {
        userRegisterService.registerUser(command);
        return ResponseEntity.ok("User registered. Check your email for activation link.");
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestBody ActivateCommand command) {
        userRegisterService.activateUser(command);
        return ResponseEntity.ok("User activated. You can log in.");
    }
}
