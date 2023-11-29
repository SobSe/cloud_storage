package ru.sobse.cloud_storage.exeption;

public class TokenNotFound extends RuntimeException {
    public TokenNotFound(String message) {
        super(message);
    }

    @Override
    public boolean equals(Object obj) {
        return this.getClass() == obj.getClass();
    }
}
