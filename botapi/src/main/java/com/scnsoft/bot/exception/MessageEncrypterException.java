package com.scnsoft.bot.exception;

public class MessageEncrypterException extends RuntimeException {

    public MessageEncrypterException() {
    }

    public MessageEncrypterException(String message) {
        super(message);
    }

    public MessageEncrypterException(Throwable cause) {
        super(cause);
    }

    public MessageEncrypterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageEncrypterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
