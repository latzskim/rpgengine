package com.example.rpgengine.user.domain.port.in;

import com.example.rpgengine.user.domain.port.in.command.ActivateCommand;
import com.example.rpgengine.user.domain.port.in.command.RegisterCommand;

public interface RegistrationServicePort {
    void registerUser(RegisterCommand command);

    void activateUser(ActivateCommand command);
}
