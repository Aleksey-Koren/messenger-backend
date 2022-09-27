package com.example.whisper.service;

import com.example.whisper.entity.Message;

import java.util.List;

public interface UtilMessageService {

    List<Message> processMessages(List<Message> messages);
}
