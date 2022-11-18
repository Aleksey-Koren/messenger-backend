package com.example.whisper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.whisper.service.gateway.MessageGateway;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bots")
@Slf4j
public record BotController(MessageGateway gateway) {

    @GetMapping 
    ResponseEntity<String> getMessage() {
        String message = "Some message";
        return ResponseEntity.ok(message);
    }

    // @PostMapping
    // public ResponseEntity<String> addMessageToQueue(@Payload String message) {
    //     message = "Some new message";
    //     this.gateway.broadcastMessage(message);
    //     return ResponseEntity.ok(message);
    // }

}
