package com.example.whisper.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class DeleteRoleRequestDto {

    @NotNull
    private UUID customerId;

    @NotNull
    private UUID chatId;

    @NotNull
    private UUID administratorId;

}
