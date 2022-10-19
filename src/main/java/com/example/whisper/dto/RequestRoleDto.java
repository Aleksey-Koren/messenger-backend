package com.example.whisper.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RequestRoleDto {
    private UUID chatId;
    private UUID customerId;
    private String role;
}
