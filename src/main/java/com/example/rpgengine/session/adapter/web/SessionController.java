package com.example.rpgengine.session.adapter.web;

import com.example.rpgengine.session.domain.exception.SessionValidationException;
import com.example.rpgengine.session.domain.port.in.SessionCommandServicePort;
import com.example.rpgengine.session.domain.port.in.SessionViewQueryServicePort;
import com.example.rpgengine.session.domain.port.in.command.CreateSessionCommand;
import com.example.rpgengine.session.domain.port.in.command.JoinSessionCommand;
import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.SessionId;
import com.example.rpgengine.session.domain.valueobject.SessionUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/sessions")
class SessionController {
    public static final String ACCESS_DENIED = "access-denied";

    private final SessionCommandServicePort sessionCommandServicePort;
    private final SessionViewQueryServicePort sessionViewQueryServicePort;
    private final UserPort userPort;

    SessionController(
            SessionCommandServicePort sessionCommandServicePort,
            SessionViewQueryServicePort sessionViewQueryServicePort,
            UserPort userPort
    ) {
        this.sessionCommandServicePort = sessionCommandServicePort;
        this.sessionViewQueryServicePort = sessionViewQueryServicePort;
        this.userPort = userPort;
    }

    @GetMapping("/create")
    String createForm(Principal principal, Model model) {
        return getSessionUser(principal).map(sessionUser -> {
            model.addAttribute("createSessionCommand", new CreateSessionCommand(
                    sessionUser.id(),
                    "",
                    "",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ));
            return "sessions/create";
        }).orElse(ACCESS_DENIED);
    }

    @PostMapping
    String createSession(
            @ModelAttribute CreateSessionCommand command,
            Principal principal,
            Model model
    ) {
        return getSessionUser(principal).map(sessionUser -> {
            try {
                var sessionId = sessionCommandServicePort.createSession(new CreateSessionCommand(
                        sessionUser.id(),
                        command.title(),
                        command.description(),
                        command.startDate(),
                        command.durationInMinutes(),
                        command.difficultyLevel(),
                        command.visibility(),
                        command.minPlayers(),
                        command.maxPlayers()
                ));

                return "redirect:/sessions/" + sessionId.getId().toString();
            } catch (SessionValidationException e) {
                model.addAttribute("errorMessage", e.getMessage());
            }

            // TODO: catch exception + custom "something went wrong" page?
            return "sessions/create";
        }).orElse(ACCESS_DENIED);
    }

    @GetMapping("/{id}")
    String sessionDetail(@PathVariable String id, Model model, Principal principal) {
        return getSessionUser(principal).map(sessionUser -> {
            var sessionReadModel = sessionViewQueryServicePort.getSessionsByUserId(
                    sessionUser.id()
            ).stream().filter(session -> session.id().equals(id)).findFirst().get();
            model.addAttribute("ses", sessionReadModel);
            return "sessions/detail";
        }).orElse(ACCESS_DENIED);
    }

    @PostMapping("/{id}/join")
    String joinToSession(
            @PathVariable String id,
            @RequestParam(required = false, name = "invite_code") String inviteCode,

            Principal principal) {
        return getSessionUser(principal).map(sessionUser -> {
            sessionCommandServicePort.join(new JoinSessionCommand(
                    SessionId.fromString(id),
                    sessionUser.id(),
                    inviteCode
            ));
            return "redirect:/sessions/" + id;
        }).orElse(ACCESS_DENIED);
    }


    private Optional<SessionUser> getSessionUser(Principal principal) {
        return userPort.findByUsername(principal.getName());
    }
}
