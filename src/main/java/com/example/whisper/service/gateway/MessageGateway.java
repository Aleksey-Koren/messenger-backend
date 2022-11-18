package com.example.whisper.service.gateway;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import com.example.whisper.service.producer.MessageProducerChannels;

// @MessagingGateway
public interface MessageGateway {
    // @Gateway(requestChannel = Source.OUTPUT)
    // void broadcastMessage(String message);
}
