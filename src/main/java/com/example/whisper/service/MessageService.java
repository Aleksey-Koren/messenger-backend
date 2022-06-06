package com.example.whisper.service;

import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final MessageRepository messageRepository;

    public ResponseEntity<List<Message>> sendMessage(List<Message> messages) {

        if (!isValid(messages)) {
            return ResponseEntity.badRequest().build();
        }

        setInstantData(messages);

        Message commonData = messages.get(0);

        if (Message.MessageType.whisper.equals(commonData.getType())) {
            return ResponseEntity.ok(messageRepository.saveAll(messages));
        }

        if (Message.MessageType.who.equals(commonData.getType())) {

            List<Message> mess = messageRepository.findByChatAndSenderAndReceiverAndType(
                    commonData.getChat(),
                    commonData.getSender(),
                    commonData.getReceiver(),
                    commonData.getType()
            );

            if (!mess.isEmpty()) {
                return ResponseEntity.ok(new ArrayList<>());
            } else {
                return ResponseEntity.ok(messageRepository.saveAll(messages));
            }
        }

        if (Message.MessageType.hello.equals(commonData.getType())) {
            List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
            List<Message> helloMessages = messageRepository.findAllByChatAndTypeAndAndReceiverIn(commonData.getChat(), Message.MessageType.hello, receivers);
            messageRepository.deleteAll(helloMessages);
            return ResponseEntity.ok(messageRepository.saveAll(messages));
        }

        if (Message.MessageType.iam.equals(commonData.getType())) {
            List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());

            messageRepository.deleteAllByChatAndSenderInAndReceiverAndType(

                    commonData.getChat(),
                    receivers,
                    commonData.getSender(),
                    Message.MessageType.who);

            return ResponseEntity.ok(messageRepository.saveAll(messages));
        }

        log.warn("Unknown type of message");
        return ResponseEntity.badRequest().build();
    }

    public List<Message> findChats(UUID receiver) {
        return messageRepository.findChats(receiver, Message.MessageType.hello);
    }

    public void updateUserTitle(List<Message> messages) {
        if (!isValidUpdateTitle(messages)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Message commonData = messages.get(0);
        messageRepository.deleteAllBySenderAndType(commonData.getSender(), Message.MessageType.iam);
        setInstantData(messages);
        messageRepository.saveAll(messages);
    }

    private void setInstantData(List<Message> messages) {
        Instant now = Instant.now();
        if (messages.get(0).getChat() == null) {
            UUID chatId = UUID.randomUUID();
            for (Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
                message.setChat(chatId);
            }
        } else {
            for (Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
            }
        }
    }

    private boolean isValid(List<Message> messages) {
        if (isNotExist(messages)) {
            return false;
        }
        return areFieldsCorrect(messages);
    }

    private boolean isValidUpdateTitle(List<Message> messages) {
        if (isNotExist(messages)) {
            return false;
        }
        return areFieldsCorrectUpdateTitle(messages);
    }

    private boolean isNotExist(List<Message> messages) {
        return messages == null || messages.isEmpty();
    }

    private boolean areFieldsCorrect(List<Message> messages) {
        Message oneFromAll = messages.get(0);
        for (Message message : messages) {
            if (isEmpty(
                    message.getSender()) ||
                    isEmpty(message.getReceiver()) ||
                    message.getType() == null ||
                    isEmpty(message.getData()) ||
                    !message.getSender().equals(oneFromAll.getSender()) ||
                    oneFromAll.getChat() == null ? message.getChat() != null : !oneFromAll.getChat().equals(message.getChat())
            ) {
                return false;
            }
        }
        return true;
    }

    private boolean areFieldsCorrectUpdateTitle(List<Message> messages) {
        Message oneFromAll = messages.get(0);
        for (Message message : messages) {
            if (isEmpty(message.getSender()) ||
                    isEmpty(message.getReceiver()) ||
                    message.getType() == null ||
                    isEmpty(message.getData()) ||
                    !message.getSender().equals(oneFromAll.getSender())
            ) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    private boolean isEmpty(UUID uuid) {
        return uuid == null;
    }
}