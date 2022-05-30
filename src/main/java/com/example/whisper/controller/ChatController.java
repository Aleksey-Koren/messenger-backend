package com.example.whisper.controller;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("chats")
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;

    @GetMapping()
    public List<UUID> getChats(
            @RequestParam ("receiver") UUID receiver
    ) {
        return messageRepository.findChats(receiver);
    }

    @GetMapping("{id}/participants")
    public List<Customer> getParticipants(@PathVariable("id") UUID chatId) {
        List<UUID> participants = messageRepository.findParticipants(chatId);
        if(participants.isEmpty()) {
            return new ArrayList<>();
        } else {
            return customerRepository.findAll(((root, query, criteriaBuilder) -> root.get("id").in(participants)));
        }
    }
}
