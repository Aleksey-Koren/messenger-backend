package com.example.whisper.service;

import com.example.whisper.dto.RequestRoleDto;
import com.example.whisper.entity.UserRole;

import java.util.List;
import java.util.UUID;

public interface UserRoleService {

    List<UserRole> findAllByChatId(UUID chatId);

    UserRole createRoleByCustomerIdAndChatId(UUID customerId, UUID chatId, RequestRoleDto roleDto);

    void deleteRoleByCustomerIdAndChatId(UUID customerId, UUID chatId);

}
