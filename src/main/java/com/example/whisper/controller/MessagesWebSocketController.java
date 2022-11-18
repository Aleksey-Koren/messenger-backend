package com.example.whisper.controller;

import com.example.whisper.entity.Message;
import com.example.whisper.service.gateway.MessageGateway;
import com.example.whisper.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
@EnableBinding(Source.class)
@Slf4j
public class MessagesWebSocketController {

    private final MessageServiceImpl messageService;
    private final Source source;

    @MessageMapping("/chat/addUser")
    public void addUser(@Payload String uuid, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("uuid", uuid);
    }


    // TODO: add check for bot existence and send bot to this
    @MessageMapping("/chat/send-message/{iam}")
    public void sendChatMessage(@Payload List<Message> messages, @DestinationVariable UUID iam) {
        messageService.sendMessage(messages);

        // TODO: Add check for bot existence
        source.output().send(MessageBuilder.withPayload(messages).build());
    }

}
