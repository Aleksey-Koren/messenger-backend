package com.scnsoft.bot.exception;

public class InvalidBotCredentialsException extends Exception {

    public InvalidBotCredentialsException() {
    }

    public InvalidBotCredentialsException(String message) {
        super(message);
    }

    public InvalidBotCredentialsException(Throwable cause) {
        super(cause);
    }

    public InvalidBotCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBotCredentialsException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
