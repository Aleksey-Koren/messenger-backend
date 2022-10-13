package com.example.whisper.service;

import com.example.whisper.entity.Administrator;

import java.util.UUID;

public interface AdministratorService {

    Administrator createRole(UUID customerId, UUID chatId, Administrator.UserType role);

    void deleteRole(UUID customerId, UUID chatId, UUID administratorId);
}
