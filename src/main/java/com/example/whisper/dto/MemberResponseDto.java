package com.example.whisper.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class MemberResponseDto {

    private UUID id;

    private String pk;

    private String role;

}
