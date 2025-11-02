package com.example.rpgengine;

import com.example.rpgengine.session.domain.port.out.SessionRepositoryPort;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class RpgEngine {
    static void main(String[] args) {
        SpringApplication.run(RpgEngine.class, args);
    }
}


@Controller
class IndexController {
    // TODO: do not use repo port :)
    private final SessionRepositoryPort sessionRepositoryPort;

    IndexController(SessionRepositoryPort sessionRepositoryPort) {
        this.sessionRepositoryPort = sessionRepositoryPort;
    }

    @GetMapping("/")
    String index(Model model) {
        model.addAttribute("sessions", sessionRepositoryPort.findAll());
        return "index";
    }

    @GetMapping("/access-denied")
    String accessDenied() {
        return "access-denied";
    }
}