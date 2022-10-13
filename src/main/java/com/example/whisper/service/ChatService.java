package com.example.whisper.service;

import com.example.whisper.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChatService {

    Chat findById(UUID uuid);

    Page<Chat> findAllWhereCustomerIdIsMember(UUID id, Pageable pageable);

    Chat create(Chat chat);

    Chat addCustomerToChat(UUID customerId, UUID chatId);

}
