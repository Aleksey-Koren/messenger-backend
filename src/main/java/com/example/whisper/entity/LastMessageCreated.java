package com.example.whisper.entity;

import java.time.Instant;
import java.util.UUID;

public interface LastMessageCreated {
    UUID getChat();

    Instant getCreated();
}
