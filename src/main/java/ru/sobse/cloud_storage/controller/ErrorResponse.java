package ru.sobse.cloud_storage.controller;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private String message;
    private int id;

    public ErrorResponse(String message, int id) {
        this.message = message;
        this.id = id;
    }
}
