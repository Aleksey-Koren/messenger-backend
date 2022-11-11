package com.example.whisper.service.impl;

import com.example.whisper.entity.Administrator;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.entity.ServerMessageType;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.repository.UtilRepository;
import com.example.whisper.service.ServerMessageService;
import com.example.whisper.service.util.DecoderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerMessageServiceImpl implements ServerMessageService {

    private final DecoderUtil decoderUtil;
    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;
    private final AdministratorRepository administratorRepository;
    private final ChatRepository chatRepository;

    public ServerMessageType getServerMessageType(String data) {
        String[] split = data.split(";");
        ServerMessageType type;

        try {
            type = ServerMessageType.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown server message type --- {}", split[0]);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return type;
    }

    public Message decryptServerMessage(List<Message> messages) {
        if (messages.size() != 1) {
            log.warn("Server message is not a single in request");
            throw new RuntimeException("Messages of type \"server\" have to be only one in request");
        } else {
            Message message = messages.get(0);
            String decrypted = decoderUtil.decryptSecretText(
                    message.getSender(),
                    message.getData(),
                    message.getNonce());

            message.setData(decrypted);
            return message;
        }
    }

    // Now nothing here, because checking token with @PreAuthorize
    @Transactional
    public void processServerMessage(Message decrypted) {
        String data = decrypted.getData();
        String[] split = data.split(";");
        ServerMessageType type;

        try {
            type = ServerMessageType.valueOf(split[0]);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown server message type --- {}", split[0]);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

//        UUID senderId = decrypted.getSender();
//        UUID chatId = decrypted.getChat();
//        switch (type) {
//
//        }
    }

}