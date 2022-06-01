package com.example.whisper.controller;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("chats")
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final MessageService messageService;

    @GetMapping()
    public List<Message> getChats(@RequestParam ("receiver") UUID receiver) {
        return messageService.findChats(receiver);
    }

    @GetMapping("{id}/participants")
    public List<Customer> getParticipants(@PathVariable("id") UUID chatId) {
        List<Message> messages = messageRepository.findAllByChatAndType(chatId, Message.MessageType.iam);
        List<UUID> participants = messages.stream().map(Message::getSender).collect(Collectors.toList());
        if(participants.isEmpty()) {
            return new ArrayList<>();
        } else {
            return customerRepository.findAll(((root, query, criteriaBuilder) -> root.get("id").in(participants)));
        }
    }
}
