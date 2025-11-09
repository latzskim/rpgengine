package com.example.rpgengine.session.domain.exception;

public class SessionStatusException extends IllegalStateException {
    public SessionStatusException(String message) {
        super(message);
    }
}
