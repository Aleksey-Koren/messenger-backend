package com.scnsoft.bot.dto;

import java.util.Set;
import java.util.UUID;

public record ChatDto(
    UUID id, 
    UUID creatorId, 
    Set<CustomerDto> members
) { 
}

