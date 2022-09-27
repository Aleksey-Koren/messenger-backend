package com.example.whisper.service.impl;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.ServerMessageType;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.UtilRepository;
import com.example.whisper.service.ServerMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerMessageServiceImpl implements ServerMessageService {

    private final CryptServiceImpl cryptService;
    private final UtilRepository utilRepository;
    private final CustomerRepository customerRepository;

    public String decryptSecretText(UUID senderId, String secretText, String nonce) {
        Customer sender = customerRepository.findById(senderId).orElseThrow(() -> {
            log.warn("Sender with id = {} doesn't exist in database", senderId);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST);
        });

        Utility secretKey = utilRepository.findById(Utility.Key.SERVER_USER_SECRET_KEY.name()).orElseThrow(() -> {
            log.warn("Server user secret key doesn't exist in database");
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        });

        Base64.Decoder decoder = Base64.getDecoder();

        return cryptService.decrypt(decoder.decode(secretText),
                decoder.decode(sender.getPk()),
                decoder.decode(nonce),
                decoder.decode(secretKey.getUtilValue()));
    }

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

}