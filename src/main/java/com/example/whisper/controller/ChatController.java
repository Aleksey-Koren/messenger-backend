package com.example.whisper.controller;

import com.example.whisper.dto.CreateChatRequestDto;
import com.example.whisper.dto.LeaveChatRequestDto;
import com.example.whisper.dto.UpdateChatTitleRequestDto;
import com.example.whisper.entity.Chat;
import com.example.whisper.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> findAllById(@PathVariable UUID chatId) {
        return new ResponseEntity<>(chatService.findById(chatId), HttpStatus.OK);
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Page<Chat>> findAllWhereCustomerIsMember(@PathVariable UUID customerId, Pageable pageable) {
        return new ResponseEntity<>(chatService.findAllWhereCustomerIdIsMember(customerId, pageable), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<Chat> create(@RequestBody CreateChatRequestDto requestDto) {
        return new ResponseEntity<>(chatService.create(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{chatId}/title")
    public ResponseEntity<Chat> updateTitleById(@PathVariable UUID chatId,
                                                @RequestBody @Valid UpdateChatTitleRequestDto requestDto) {
        return new ResponseEntity<>(chatService.updateTitleById(chatId, requestDto.getTitle()), HttpStatus.OK);
    }

    @PutMapping("/{chatId}/customers/{customerId}")
    public ResponseEntity<Chat> addCustomerToChat(@PathVariable UUID chatId, @PathVariable UUID customerId) {
        return new ResponseEntity<>(chatService.addCustomer(chatId, customerId), HttpStatus.OK);
    }

    @DeleteMapping("/{chatId}/customers/{customerId}")
    public ResponseEntity<Chat> removeCustomerFromChat(@PathVariable UUID chatId,
                                                       @PathVariable UUID customerId,
                                                       @RequestBody LeaveChatRequestDto requestDto) {
        return new ResponseEntity<>(
                chatService.removeCustomer(chatId, customerId, requestDto.getSecretText(), requestDto.getNonce()),
                HttpStatus.OK);
    }

}
