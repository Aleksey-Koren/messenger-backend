package com.example.whisper.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class CreateChatRequestDto {

    @NotEmpty
    private String title;

    @NotNull
    private UUID creatorId;
}
