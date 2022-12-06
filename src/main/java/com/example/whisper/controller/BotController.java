package com.example.whisper.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.whisper.entity.Bot;
import com.example.whisper.entity.Message;
import com.example.whisper.service.BotService;
import com.example.whisper.service.MessageService;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/bots")
@EnableBinding(Processor.class)
public record BotController(
    MessageService messageService,
    BotService botService
) {

    @PostMapping
    public ResponseEntity<Bot> register(@RequestBody @Valid Bot bot) {
        log.info("Registering bot: {}", bot);
        return new ResponseEntity<>(botService.register(bot), HttpStatus.CREATED);
    }

    @StreamListener(target = Processor.INPUT)
	public void respond(Message message) {
        Bot bot = botService.findById(message.getReceiver());

        HttpEntity<Message> request = new HttpEntity<>(message);
        RestTemplate restTemplate = new RestTemplate();
        String botUrl = bot.getWebhookUrl() + "messages";
        ResponseEntity<List<Message>> response = restTemplate.exchange(
            botUrl, 
            HttpMethod.POST, 
            request, 
            new ParameterizedTypeReference<List<Message>>() {}
        );

        List<Message> messages = response.getBody();
        messageService.sendMessage(messages);
	}
}
