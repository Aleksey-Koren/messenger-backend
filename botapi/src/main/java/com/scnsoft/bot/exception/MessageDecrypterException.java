package com.scnsoft.bot.exception;

public class MessageDecrypterException extends RuntimeException {

    public MessageDecrypterException() {
    }

    public MessageDecrypterException(String message) {
        super(message);
    }

    public MessageDecrypterException(Throwable cause) {
        super(cause);
    }

    public MessageDecrypterException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageDecrypterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
