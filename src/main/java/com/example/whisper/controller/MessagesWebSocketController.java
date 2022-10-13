package com.example.whisper.controller;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.entity.Utility;
import com.example.whisper.service.impl.MessageServiceImpl;
import com.example.whisper.service.impl.SecretMessageUtil;
import com.example.whisper.service.impl.ServerMessageServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessagesWebSocketController {

    private final MessageServiceImpl messageService;
    private final SecretMessageUtil secretMessageUtil;

    @MessageMapping("/chat/addUser")
    public void addUser(@Payload String uuid, SimpMessageHeaderAccessor headerAccessor) {
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("uuid", uuid);
    }

    @MessageMapping("/chat/send-message/{iam}")
    public void sendChatMessage(@Payload List<Message> messages, @DestinationVariable UUID iam) {
        System.out.println("----------------SOCKET----------------------------");
        System.out.println("iam = " + iam);
        System.out.println(messages);
        messageService.oldSendMessage(messages, iam);
    }

    @MessageMapping("/test/")
    public void test(@Payload List<Message> messages,
                     @RequestHeader("test") String language) {

//        headers.forEach((key, value) -> {
//            System.out.printf("Header '%s' = %s%n", key, value);
//        });
//        System.out.println("ENCRYPT TOKEN = " + token);

//        String decrypt = secretMessageUtil
//                .decryptSecretText(UUID.fromString("5ce5ac54-3586-42e1-88e2-ae294f075f44"), token, nonce);

//        System.out.println("ENCRYPT TOKEN = " + decrypt);
    }


}
