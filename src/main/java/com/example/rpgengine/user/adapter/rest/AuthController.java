package com.example.rpgengine.user.adapter.rest;

import com.example.rpgengine.user.domain.port.in.AuthServicePort;
import com.example.rpgengine.user.domain.port.in.InvalidCredentialsException;
import com.example.rpgengine.user.domain.port.in.UserNotActiveException;
import com.example.rpgengine.user.domain.port.in.command.LoginCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthServicePort authService;

    public AuthController(AuthServicePort authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticateUser(@RequestBody LoginCommand loginCommand) {
        try {
            var token = authService.authenticateUser(loginCommand);
            return ResponseEntity.ok(token);
        } catch (UserNotActiveException | InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}