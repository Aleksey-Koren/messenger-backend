package com.example.whisper.service;

import com.example.whisper.dto.RequestRoleDto;
import com.example.whisper.entity.Administrator;

import java.util.List;
import java.util.UUID;

public interface AdministratorService {

    List<Administrator> findAllByChatId(UUID chatId);

    Administrator createRoleByCustomerIdAndChatId(RequestRoleDto roleDto);

    void deleteRoleByCustomerIdAndChatId(UUID customerId, UUID chatId);

}
