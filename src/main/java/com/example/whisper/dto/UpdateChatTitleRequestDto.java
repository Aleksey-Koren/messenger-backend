package com.example.whisper.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateChatTitleRequestDto {

    @NotEmpty
    private String title;
}
