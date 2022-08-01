package com.example.whisper.entity;

import lombok.Getter;

@Getter
public enum ServerMessageType {

    LEAVE_CHAT("LEAVE_CHAT");

    ServerMessageType(String value) {
        this.value = value;
    }

    private final String value;
}