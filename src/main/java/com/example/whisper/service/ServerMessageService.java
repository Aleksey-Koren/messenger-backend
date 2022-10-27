package com.example.whisper.service;

import com.example.whisper.entity.ServerMessageType;

import java.util.UUID;

public interface ServerMessageService {

    ServerMessageType getServerMessageType(String data);

}
