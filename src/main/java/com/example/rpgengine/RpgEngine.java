package com.example.rpgengine;

import com.example.rpgengine.session.domain.port.in.SessionViewQueryServicePort;
import com.example.rpgengine.session.domain.port.in.query.SessionsEligibleToPlayQuery;
import com.example.rpgengine.session.domain.port.in.query.SortBy;
import com.example.rpgengine.session.domain.port.in.query.SortDirection;
import com.example.rpgengine.session.domain.port.in.query.SortField;
import com.example.rpgengine.session.domain.valueobject.SessionStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@EnableScheduling
public class RpgEngine {
    static void main(String[] args) {
        SpringApplication.run(RpgEngine.class, args);
    }
}


@Controller
class IndexController {
    private final SessionViewQueryServicePort sessionViewQueryServicePort;

    IndexController(SessionViewQueryServicePort sessionViewQueryServicePort) {
        this.sessionViewQueryServicePort = sessionViewQueryServicePort;
    }

    @GetMapping("/")
    String index(Model model) {
        var defaultSessions = sessionViewQueryServicePort.getSessionsEligibleToPlay(new SessionsEligibleToPlayQuery(
                SessionStatus.SCHEDULED,
                null,
                0, // TODO ?
                20,// TODO: ?
                new SortBy(SortField.STARTING_AT, SortDirection.ASC) // newest first.
        ));

        model.addAttribute("sessions", defaultSessions);
        return "index";
    }

    @GetMapping("/access-denied")
    String accessDenied() {
        return "access-denied";
    }
}