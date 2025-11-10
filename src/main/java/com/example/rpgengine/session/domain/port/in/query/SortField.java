package com.example.rpgengine.session.domain.port.in.query;

public enum SortField {
    STARTING_AT("startDate"),
    CREATED_AT("createdAt");

    private final String fieldName;

    SortField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return this.fieldName;
    }
}
