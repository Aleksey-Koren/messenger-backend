package com.example.whisper.dto;

import com.example.whisper.entity.Administrator;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateRoleRequestDto {

    @NotNull
    private UUID customerId;
    @NotNull
    private UUID chatId;
    @NotNull
    private UUID administratorId;

    private Administrator.UserType role;
}
