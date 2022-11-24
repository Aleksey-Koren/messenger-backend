package com.example.whisper.controller;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.http.HttpEntity;
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
import com.example.whisper.service.impl.ChatServiceImpl;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bots")
@EnableBinding(Processor.class)
@Slf4j
public record BotController(
    CustomerService customerServie, 
    BotService botService, 
    ChatServiceImpl chatService) {

    private static final String SOURCE_APPLICATION_URL = "http://localhost:8080/";

    @StreamListener(target = Processor.INPUT)
    public void respond(Message message) {
        log.info(Processor.class + " works fine");
        // Message newMessage = new Message();
        // HttpEntity<Message> request = new HttpEntity<>(newMessage);
        // RestTemplate restTemplate = new RestTemplate();
        // String botUrl = SOURCE_APPLICATION_URL + "bots";
        // restTemplate.postForEntity(botUrl, request, Message.class, "");
	}

    // @PostMapping
    // public Message getMessage(Message message) {
    //     log.info("Yuhuuuu, I've received message: " + message);
    //     return message;
    // }

    // @GetMapping 
    // ResponseEntity<String> getMessage() {
    //     String message = "Some message";
    //     return ResponseEntity.ok(message);
    // }

    // @PostMapping
    // public ResponseEntity<String> addMessageToQueue(@Payload String message) {
    //     message = "Some new message";
    //     this.gateway.broadcastMessage(message);
    //     return ResponseEntity.ok(message);
    // }

    // @GetMapping("/all")
    // public List<Customer> findAll() {
    //     List<Customer> allCustomers = Collections.emptyList();
    //     try {
    //         Customer customer = new Customer();
    //         customer.setId(UUID.randomUUID());
    //         customer.setPk("sdf342");
    //         customerServie.register(customer);
    
    //         Chat chat = new Chat(UUID.randomUUID(), customer.getId(), null);
    //         chatService.create(chat);
    
            // Bot bot = new Bot();
            // bot.setId(UUID.randomUUID());
            // bot.setPk("sdfsfsdfd");
            // botService.register(bot);
            // chatService.addCustomerToChat(bot.getId(), chat.getId());
    
    
    //         allCustomers = chatService.findChatBots(chat.getId());
    //     } catch (Exception e) {
    //         System.out.println(e.getMessage());
    //     }

    //     return allCustomers;
    // }

}
