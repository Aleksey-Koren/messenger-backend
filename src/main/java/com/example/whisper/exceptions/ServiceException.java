package com.example.whisper.exceptions;

public class ServiceException extends RuntimeException {

    public ServiceException() {

    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
