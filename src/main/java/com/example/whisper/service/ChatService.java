package com.example.whisper.service;

import com.example.whisper.dto.CreateChatRequestDto;
import com.example.whisper.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChatService {

    Chat findById(UUID uuid);

    Page<Chat> findAllWhereCustomerIdIsMember(UUID id, Pageable pageable);

    Chat create(CreateChatRequestDto createChatRequestDto);

    Chat updateTitleById(UUID id, String title);

    Chat addCustomer(UUID chatId, UUID customerId);

    Chat removeCustomer(UUID chatId, UUID customerId, String secretText, String nonce);
}
