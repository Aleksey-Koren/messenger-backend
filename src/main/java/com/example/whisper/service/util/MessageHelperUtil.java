package com.example.whisper.service.util;

import com.example.whisper.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class MessageHelperUtil {

    public static void setInstantData(List<Message> messages) {
        Instant now = Instant.now();
        if (messages.get(0).getChat() == null) {
            UUID chatId = UUID.randomUUID();
            for (Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
                message.setChat(chatId);
            }
        } else {
            for (Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
            }
        }
    }

}
