package com.example.whisper.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LeaveChatRequestDto {

    @NotEmpty
    private String secretText;

    @NotEmpty
    private String nonce;
}
