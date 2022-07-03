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

    public ResponseEntity<List<Message>> sendMessage(List<Message> messages, UUID iam) {

        if (!isValid(messages)) {
            return ResponseEntity.badRequest().build();
        }

        setInstantData(messages);

        Message controlMessage = messages.get(0);
        List<Message> out;
        if (Message.MessageType.whisper.equals(controlMessage.getType())) {
            out = messageRepository.saveAll(messages);
        } else if (Message.MessageType.who.equals(controlMessage.getType())) {

            List<Message> mess = messageRepository.findByChatAndSenderAndReceiverAndType(
                    controlMessage.getChat(),
                    controlMessage.getSender(),
                    controlMessage.getReceiver(),
                    controlMessage.getType()
            );

            if (!mess.isEmpty()) {
                out = new ArrayList<>();
            } else {
                out = messageRepository.saveAll(messages);
            }
        } else if (Message.MessageType.hello.equals(controlMessage.getType())) {
            List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
            if(!receivers.isEmpty()) {
                messageRepository.deleteHelloMessages(
                        controlMessage.getChat(),
                        Message.MessageType.hello,
                        receivers
                );
            }
            out = messageRepository.saveAll(messages);
        } else if (Message.MessageType.iam.equals(controlMessage.getType())) {
            List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());

            messageRepository.deleteAllByChatAndSenderInAndReceiverAndType(

                    controlMessage.getChat(),
                    receivers,
                    controlMessage.getSender(),
                    Message.MessageType.who);

            out = messageRepository.saveAll(messages);
        } else {
            out = new ArrayList<>();
            log.warn("Unknown type of message");
            ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(out.stream()
                .filter(message -> message.getReceiver().equals(iam))
                .collect(Collectors.toList()));
    }

    public List<Message> findChats(UUID receiver) {
        return messageRepository.findChats(receiver, Message.MessageType.hello);
    }

    public List<Message> updateUserTitle(List<Message> messages) {
        if (!isValidUpdateTitle(messages)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Message commonData = messages.get(0);
        messageRepository.deleteAllBySenderAndType(commonData.getSender(), Message.MessageType.iam);
        setInstantData(messages);
        return messageRepository.saveAll(messages);
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
        if (isEmpty(messages)) {
            return false;
        }
        return areFieldsCorrect(messages);
    }

    private boolean isValidUpdateTitle(List<Message> messages) {
        if (isEmpty(messages)) {
            return false;
        }
        return areFieldsCorrectUpdateTitle(messages);
    }

    private boolean isEmpty(List<Message> messages) {
        return messages == null || messages.isEmpty();
    }

    private boolean areFieldsCorrect(List<Message> messages) {
        Message oneFromAll = messages.get(0);
        for (Message message : messages) {
            if (isEmpty(message.getSender()) ||
                    isEmpty(message.getReceiver()) ||
                    message.getType() == null ||
                    (
                            !(Message.MessageType.hello.equals(message.getType())
                                    || Message.MessageType.who.equals(message.getType()))
                                    && isEmpty(message.getData())) ||
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