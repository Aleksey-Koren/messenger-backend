package com.example.whisper.service;

import com.example.whisper.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {

    Page<Message> findAllByParams(UUID receiver,
                                  Instant created,
                                  Instant before,
                                  Message.MessageType type,
                                  UUID chat,
                                  Pageable pageable);

    void sendMessage(List<Message> messages);

    List<Message> findOld();

    void deleteAll(List<Message> messages);
}
