package com.example.whisper.entity;

import lombok.Getter;

@Getter
public enum ServerMessageType {
    LEAVE_ROOM("LEAVE_ROOM");

    ServerMessageType(String value) {
        this.value = value;
    }

    private final String value;
}
