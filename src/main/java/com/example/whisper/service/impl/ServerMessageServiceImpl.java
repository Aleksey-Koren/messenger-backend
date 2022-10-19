package com.example.whisper.service.impl;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.entity.ServerMessageType;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.repository.UtilRepository;
import com.example.whisper.service.ServerMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerMessageServiceImpl implements ServerMessageService {

    private final CryptServiceImpl cryptService;
    private final UtilRepository utilRepository;
    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;

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

    public Message decryptServerMessage(List<Message> messages) {
        if (messages.size() != 1) {
            log.warn("Server message is not a single in request");
            throw new RuntimeException("Messages of type \"server\" have to be only one in request");
        } else {
            Message message = messages.get(0);

            Customer sender = customerRepository.findById(message.getSender()).orElseThrow(() -> {
                log.warn("Sender with id = {} doesn't exist in database", message.getSender());
                return new ResponseStatusException(HttpStatus.BAD_REQUEST);
            });

            Utility secretKey = utilRepository.findById(Utility.Key.SERVER_USER_SECRET_KEY.name()).orElseThrow(() -> {
                log.warn("Server user secret key doesn't exist in database");
                return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            });

            Base64.Decoder decoder = Base64.getDecoder();
            String decrypted = cryptService.decrypt(decoder.decode(message.getData()),
                    decoder.decode(sender.getPk()),
                    decoder.decode(message.getNonce()),
                    decoder.decode(secretKey.getUtilValue()));
            message.setData(decrypted);
            return message;
        }
    }

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

        switch (type) {
            case LEAVE_CHAT -> {
                messageRepository
                        .deleteAllByReceiverAndChatAndType(
                                decrypted.getSender(),
                                decrypted.getChat(),
                                Message.MessageType.iam);
                messageRepository
                        .deleteAllBySenderAndChatAndType(
                                decrypted.getSender(),
                                decrypted.getChat(),
                                Message.MessageType.iam);
                messageRepository
                        .deleteAllByReceiverAndChatAndType(
                                decrypted.getSender(),
                                decrypted.getChat(),
                                Message.MessageType.hello);
            }
            case LEAVE_CHAT_WITH_DELETE_OWN_MESSAGES -> {
                messageRepository.deleteAllByReceiverAndSenderAndChat(
                        decrypted.getSender(),
                        decrypted.getSender(),
                        decrypted.getChat());
            }
        }
    }

}