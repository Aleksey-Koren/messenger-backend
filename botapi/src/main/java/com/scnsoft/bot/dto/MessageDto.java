package com.scnsoft.bot.dto;

import java.time.Instant;
import java.util.UUID;

import com.scnsoft.bot.model.Message;

import lombok.Builder;


@Builder
public record MessageDto(
    UUID id,
    UUID sender,
    UUID receiver,
    UUID chat,
    MessageType type,
    String data,
    String attachments,
    String nonce,
    Instant created
) {
    
    public enum MessageType {
        hello,
        iam,
        whisper,
        who,
        server,
        LEAVE_CHAT,
        ASSIGN_ROLE,
    }

    public Message toMessage() {
        return new Message(data, attachments);
    }
}
