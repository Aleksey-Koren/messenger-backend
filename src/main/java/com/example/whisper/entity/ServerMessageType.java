package com.example.whisper.entity;

import lombok.Getter;

@Getter
public enum ServerMessageType {

    LEAVE_CHAT("LEAVE_CHAT"),
    LEAVE_CHAT_WITH_DELETE_OWN_MESSAGES("LEAVE_CHAT_WITH_DELETE_OWN_MESSAGES");

    ServerMessageType(String value) {
        this.value = value;
    }

    private final String value;
}