package com.example.whisper.controller;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.whisper.entity.Bot;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.service.BotService;
import com.example.whisper.service.ChatService;
import com.example.whisper.service.CustomerService;
import com.example.whisper.service.MessageService;
import com.example.whisper.service.impl.ChatServiceImpl;
import com.example.whisper.service.util.DecoderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bots")
@EnableBinding(Processor.class)
@Slf4j
public record BotController(
    CustomerService customerServie, 
    BotService botService, 
    ChatServiceImpl chatService,
    DecoderUtil decoderUtil,
    MessageService messageService) {

    private static final String BOT_APPLICATION_URL = "http://localhost:8081/";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @StreamListener(target = Processor.INPUT)
	public void respond(Message message) {
        HttpEntity<Message> request = new HttpEntity<>(message);
        RestTemplate restTemplate = new RestTemplate();
        String botUrl = BOT_APPLICATION_URL + "bot/messages";
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            botUrl, 
            HttpMethod.POST, 
            request, 
            new ParameterizedTypeReference<List<Message>>() {}
        );

        List<Message> messages = response.getBody();
        messageService.sendMessage(messages);
        // messageService.sendMessage(messages);
	}
}
