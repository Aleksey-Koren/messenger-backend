package com.example.whisper.service;

import com.example.whisper.entity.ServerMessageType;

import java.util.UUID;

public interface ServerMessageService {

    String decryptSecretText(UUID senderId, String secretText, String nonce);

    ServerMessageType getServerMessageType(String data);

}
