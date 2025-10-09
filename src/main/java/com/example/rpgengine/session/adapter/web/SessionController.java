package com.example.rpgengine.session.adapter.web;

import com.example.rpgengine.session.domain.port.out.SessionRepositoryPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/session")
class SessionController {

    private final SessionRepositoryPort sessionRepositoryPort;

    SessionController(SessionRepositoryPort sessionRepositoryPort) {
        this.sessionRepositoryPort = sessionRepositoryPort;
    }

    @GetMapping
    public String listSession(Model model) {
        var sessions = sessionRepositoryPort.findAll();
        model.addAttribute("sessions", sessions);

        return "session/list";
    }
}
