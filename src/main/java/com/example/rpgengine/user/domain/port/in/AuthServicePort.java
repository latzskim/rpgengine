package com.example.rpgengine.user.domain.port.in;

import com.example.rpgengine.user.domain.port.in.command.LoginCommand;

public interface AuthServicePort {
    String authenticateUser(LoginCommand loginCommand);
}
