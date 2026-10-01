package dev.faizarfi.starter.arfiid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Deprecated
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CookieNotFoundException extends RuntimeException {
    public CookieNotFoundException(String message) {
        super(message);
    }
}
