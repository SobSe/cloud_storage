package ru.sobse.cloud_storage.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.sobse.cloud_storage.exeption.*;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ErrorResponse> userNotFound(UserNotFound e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), Errors.USER_NOT_FOUND.getType()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({PasswordIncorrect.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> passwordIncorrect(RuntimeException e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), Errors.INCORRECT_PASSWORD.getType()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenNotFound.class)
    public ResponseEntity<ErrorResponse> tokenNotFound(TokenNotFound e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), Errors.TOKEN_NOT_FOUND.getType()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ErrorStoringFile.class)
    public ResponseEntity<ErrorResponse> errorStoringFile(ErrorStoringFile e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), Errors.ERROR_STORING_FILE.getType()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotFound.class)
    public ResponseEntity<ErrorResponse> fileNotFound(FileNotFound e) {
        return new ResponseEntity<>(
                new ErrorResponse(e.getMessage(), Errors.FILE_NOTE_FOUND.getType()),
                HttpStatus.BAD_REQUEST);
    }
}
