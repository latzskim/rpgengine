package com.example.rpgengine.session.adapter.web;

import com.example.rpgengine.session.domain.exception.SessionForbiddenException;
import com.example.rpgengine.session.domain.exception.SessionNotFoundException;
import com.example.rpgengine.session.domain.exception.SessionStatusException;
import com.example.rpgengine.session.domain.exception.SessionValidationException;
import com.example.rpgengine.session.domain.port.in.SessionCommandServicePort;
import com.example.rpgengine.session.domain.port.in.SessionViewQueryServicePort;
import com.example.rpgengine.session.domain.port.in.command.*;
import com.example.rpgengine.session.domain.port.out.UserPort;
import com.example.rpgengine.session.domain.valueobject.*;
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
            model.addAttribute("createSessionCommand", new CreateSessionForm(
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

    @GetMapping("/{id}/edit")
    String editForm(@PathVariable String id, Principal principal, Model model) {
        return getSessionUser(principal).map(sessionUser -> {
            var sessionId = SessionId.fromString(id);
            var session = sessionViewQueryServicePort.getSessionByUserId(sessionId, sessionUser.id());

            var form = new UpdateSessionForm(
                    session.title(),
                    session.description(),
                    session.startDate(),
                    session.durationInMinutes().intValue(),
                    DifficultyLevel.valueOf(session.difficulty()),
                    Visibility.valueOf(session.visibility().name()),
                    session.minPlayers(),
                    session.maxPlayers()
            );

            model.addAttribute("updateSessionCommand", form);
            model.addAttribute("sessionId", id);

            return "sessions/edit";
        }).orElse(ACCESS_DENIED);
    }

    @PostMapping("/{id}/edit")
    String updateSession(
            @PathVariable String id,
            @ModelAttribute("updateSessionCommand") UpdateSessionForm form,
            Principal principal,
            Model model
    ) {
        return getSessionUser(principal).map(sessionUser -> {
            try {
                sessionCommandServicePort.updateSession(new UpdateSessionCommand(
                        SessionId.fromString(id),
                        sessionUser.id(),
                        form.getTitle(),
                        form.getDescription(),
                        form.getStartDate(),
                        form.getDurationInMinutes(),
                        form.getDifficultyLevel(),
                        form.getVisibility(),
                        form.getMinPlayers(),
                        form.getMaxPlayers()
                ));
                return "redirect:/sessions/" + id;
            } catch (SessionValidationException | SessionStatusException e) {
                model.addAttribute("errorMessage", e.getMessage());
                model.addAttribute("updateSessionCommand", form);
                model.addAttribute("sessionId", id);
                return "sessions/edit";
            }
        }).orElse(ACCESS_DENIED);
    }

    @PostMapping("/{id}/schedule")
    String scheduleSession(@PathVariable String id, Principal principal) {
        return getSessionUser(principal).map(sessionUser -> {
            sessionCommandServicePort.scheduleSession(new com.example.rpgengine.session.domain.port.in.command.ScheduleSessionCommand(
                    SessionId.fromString(id),
                    sessionUser.id()
            ));
            return "redirect:/sessions/" + id;
        }).orElse(ACCESS_DENIED);
    }

    @PostMapping("/{id}/delete")
    String deleteSession(@PathVariable String id, Principal principal) {
        return getSessionUser(principal).map(sessionUser -> {
            sessionCommandServicePort.deleteSession(new DeleteSessionCommand(
                    SessionId.fromString(id),
                    sessionUser.id()
            ));
            return "redirect:/";
        }).orElse(ACCESS_DENIED);
    }

    @PostMapping
    String createSession(
            @ModelAttribute("createSessionCommand") CreateSessionForm form,
            Principal principal,
            Model model
    ) {
        return getSessionUser(principal).map(sessionUser -> {
            try {
                var sessionId = sessionCommandServicePort.createSession(new CreateSessionCommand(
                        sessionUser.id(),
                        form.getTitle(),
                        form.getDescription(),
                        form.getStartDate(),
                        form.getDurationInMinutes(),
                        form.getDifficultyLevel(),
                        form.getVisibility(),
                        form.getMinPlayers(),
                        form.getMaxPlayers()
                ));

                return "redirect:/sessions/" + sessionId.getId().toString();
            } catch (SessionValidationException e) {
                model.addAttribute("errorMessage", e.getMessage());
                model.addAttribute("createSessionCommand", form);
            }

            // TODO: catch exception + custom "something went wrong" page?
            return "sessions/create";
        }).orElse(ACCESS_DENIED);
    }

    @GetMapping("/{id}")
    String sessionDetail(@PathVariable String id, Model model, Principal principal) {
        try {
            var userIdOrNull = getSessionUser(principal).map(SessionUser::id).orElse(null);

            var detail = sessionViewQueryServicePort.getSessionByUserId(
                    SessionId.fromString(id),
                    userIdOrNull
            );

            model.addAttribute("ses", detail);
            if (userIdOrNull != null) {
                model.addAttribute("currentUserId", userIdOrNull.getUserId().toString());
            }
            return "sessions/detail";
        } catch (SessionNotFoundException e) {
            // TODO:
        } catch (SessionForbiddenException e) {
            // TODO: ?
            return ACCESS_DENIED;
        }

        return ACCESS_DENIED;
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

    @PostMapping("/{id}/requests/{userId}")
    String handleUserJoinRequest(
            @PathVariable String id,
            @PathVariable String userId,
            @RequestParam(name = "approve") Boolean approve,
            Principal principal
    ) {
        return getSessionUser(principal).map(sessionUser -> {
            sessionCommandServicePort.handleUserJoinRequest(new HandleUserJoinSessionDecisionCommand(
                    sessionUser.id(),
                    SessionId.fromString(id),
                    UserId.fromString(userId),
                    approve
            ));
            return "redirect:/sessions/" + id;
        }).orElse(ACCESS_DENIED);
    }

//    @PostMapping("/{id}/schedule")
//    String scheduleSession() {
//
//    }


    private Optional<SessionUser> getSessionUser(Principal principal) {
        return userPort.findByUsername(principal.getName());
    }
}
