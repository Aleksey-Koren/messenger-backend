package com.example.whisper.service;

import com.example.whisper.entity.Chat;

import java.util.UUID;

public interface ChatService {

    Chat findById(UUID uuid);

    Chat create(Chat chat);

    Chat addCustomerToChat(UUID customerId, UUID chatId);

    void removeCustomerFromChat(UUID customerId, UUID chatId);

    void leaveChat(UUID chatId, UUID customerId, Boolean withDeleteMessages);

}
