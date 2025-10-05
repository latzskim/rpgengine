package com.example.rpgengine.user.adapter.rest;


import com.example.rpgengine.user.domain.port.in.RegistrationServicePort;
import com.example.rpgengine.user.domain.port.in.command.ActivateCommand;
import com.example.rpgengine.user.domain.port.in.command.RegisterCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final RegistrationServicePort userRegisterService;

    RegistrationController(RegistrationServicePort userRegisterService) {
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
