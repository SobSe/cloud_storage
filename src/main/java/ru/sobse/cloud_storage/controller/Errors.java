package ru.sobse.cloud_storage.controller;

import lombok.Getter;

@Getter
public enum Errors {
    USER_NOT_FOUND(1),
    INCORRECT_PASSWORD(2),
    TOKEN_NOT_FOUND(3),
    ERROR_STORING_FILE(4),
    FILE_NOTE_FOUND(5);
    private final int type;

    Errors(int type) {
        this.type = type;
    }
}