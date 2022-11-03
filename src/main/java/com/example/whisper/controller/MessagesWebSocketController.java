package com.example.whisper.controller;

import com.example.whisper.entity.Message;
import com.example.whisper.service.impl.MessageServiceImpl;
import com.example.whisper.service.util.DecoderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessagesWebSocketController {

    private final MessageServiceImpl messageService;

    @MessageMapping("/chat/addUser")
    public void addUser(@Payload String uuid, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("uuid", uuid);
    }

    @MessageMapping("/chat/send-message/{iam}")
    public void sendChatMessage(@Payload List<Message> messages, @DestinationVariable UUID iam) {
        messageService.sendMessage(messages);
    }

}