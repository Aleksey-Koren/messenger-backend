package com.example.whisper.controller;

import com.example.whisper.entity.Bot;
import com.example.whisper.entity.Message;
import com.example.whisper.entity.Message.MessageType;
import com.example.whisper.repository.BotRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.BotService;
import com.example.whisper.service.ChatService;
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
// @EnableBinding(Source.class)
@Slf4j
public class MessagesWebSocketController {

    private final MessageServiceImpl messageService;

    // TODO: delete this fields
    private final BotService botService;
    private final ChatService chatService;
    private final BotRepository botRepository;
    private final MessageRepository messageRepository;
    // private final Source source;

    @MessageMapping("/chat/addUser")
    public void addUser(@Payload String uuid, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("uuid", uuid);
    }


    @MessageMapping("/chat/send-message/{iam}")
    public void sendChatMessage(@Payload List<Message> messages, @DestinationVariable UUID iam) {

        // Bot bot = new Bot();
        // bot.setId(UUID.randomUUID());
        // bot.setPk("sdfsfsdfd");
        // botService.register(bot);
        // Message controlMessage = messages.get(0);
        // UUID chatId = controlMessage.getChat();
        // chatService.addCustomerToChat(bot.getId(), chatId);
        // Message message = new Message(
        //     UUID.randomUUID(), 
        //     controlMessage.getSender(), 
        //     bot.getId(), 
        //     controlMessage.getChat(), 
        //     Message.MessageType.hello, 
        //     controlMessage.getData(), 
        //     controlMessage.getAttachments(), 
        //     controlMessage.getNonce(), 
        //     controlMessage.getCreated());
        // messageRepository.save(message);

        // messages.add(message);
        log.info("incoming messages: {}", messages);

        messageService.sendMessage(messages);

    }

}
