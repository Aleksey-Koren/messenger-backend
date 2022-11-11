package com.example.whisper.service;

import com.example.whisper.entity.ServerMessageType;

public interface ServerMessageService {

    ServerMessageType getServerMessageType(String data);

}
