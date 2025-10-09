package com.example.rpgengine.session.domain.exception;

public class SessionForbiddenException extends RuntimeException {
    public SessionForbiddenException(String message) {
        super(message);
    }
}
