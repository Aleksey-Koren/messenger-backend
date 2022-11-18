package com.example.whisper.service;

import java.util.List;
import java.util.UUID;

import com.example.whisper.entity.Bot;

public interface BotService {
    Bot findById(UUID id);

    List<Bot> findAllByIds(List<UUID> ids);

    Bot register(Bot bot);

    Void delete(Bot bot);
}
