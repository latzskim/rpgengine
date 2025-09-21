package com.example.rpgengine.controller.auth;

import com.example.rpgengine.application.auth.AuthService;
import com.example.rpgengine.application.auth.InvalidCredentialsException;
import com.example.rpgengine.application.auth.UserNotActiveException;
import com.example.rpgengine.application.auth.command.LoginCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
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