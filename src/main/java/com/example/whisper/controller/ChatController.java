package com.example.whisper.controller;

import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.ChatService;
import com.example.whisper.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("chats")
@RequiredArgsConstructor
// DELETE THIS!!!
public class ChatController {

    private final ChatService chatService;
    private final MessageServiceImpl messageService;
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;

    @GetMapping()
    public List<Message> getChats(@RequestParam("receiver") UUID receiver) {
        return messageService.findChats(receiver);
    }

    @GetMapping("{id}/participants")
    public List<Customer> getParticipants(@PathVariable("id") UUID chatId) {
        List<Message> messages = messageRepository.findAllByChatAndType(chatId, Message.MessageType.hello);
        List<UUID> participants = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
        if (participants.isEmpty()) {
            return new ArrayList<>();
        } else {
            return customerRepository.findAllById(participants);
        }
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> findAllById(@PathVariable UUID chatId) {
        return new ResponseEntity<>(chatService.findById(chatId), HttpStatus.OK);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Page<Chat>> findAllWhereCustomerIsMember(@PathVariable UUID customerId, Pageable pageable) {
        return new ResponseEntity<>(chatService.findAllWhereCustomerIdIsMember(customerId, pageable), HttpStatus.OK);
    }

}
