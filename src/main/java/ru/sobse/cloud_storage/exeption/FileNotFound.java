package ru.sobse.cloud_storage.exeption;

public class FileNotFound extends RuntimeException {
    public FileNotFound(String message) {
        super(message);
    }
}
