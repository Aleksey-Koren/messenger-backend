package com.example.whisper.service;

import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public ResponseEntity<List<Message>>sendMessage(List<Message> messages) {

        if(!isValid(messages)) {
            return ResponseEntity.badRequest().build();
        }

        setInstantData(messages);

        Message commonData = messages.get(0);

        if(Message.MessageType.whisper.equals(messages.get(0).getType())) {
            List<Message> messageList = messages;
            return ResponseEntity.ok(messageRepository.saveAll(messageList));
        }

        if(Message.MessageType.who.equals(messages.get(0).getType())) {
            Message incoming = messages.get(0);
            List<Message> mess = messageRepository.findByChatAndSenderAndReceiverAndType(
                    incoming.getChat(),
                    incoming.getSender(),
                    incoming.getReceiver(),
                    incoming.getType()
            );

            if(mess.size() != 0) {
                return ResponseEntity.ok(new ArrayList<>());
            } else {
                return ResponseEntity.ok(messageRepository.saveAll(messages));
            }
        }

        if(Message.MessageType.hello.equals(commonData.getType())) {
            List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
            List<Message> toDelete = messageRepository.findAllByChatAndTypeAndAndReceiverIn(commonData.getChat(), Message.MessageType.hello, receivers);
            //todo deleteAllByChatAndType
            messageRepository.deleteAll(toDelete);
            return ResponseEntity.ok(messageRepository.saveAll(messages));
        }

        if(Message.MessageType.iam.equals(messages.get(0).getType())) {
            Message incoming = messages.get(0);
            List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
            UUID chat = messages.get(0).getChat();
            UUID sender = messages.get(0).getSender();

            messageRepository.deleteAllByChatAndSenderInAndReceiverAndType(
                    incoming.getChat(),
                    receivers,
                    incoming.getSender(),
                    Message.MessageType.who);

            return ResponseEntity.ok(messageRepository.saveAll(messages));
        }

        log.warn("Unknown type of message");
        return ResponseEntity.badRequest().build();
    }

    public List<Message> findChats(UUID receiver) {
        return messageRepository.findChats(receiver, Message.MessageType.hello);
    }

    private void setInstantData(List<Message> messages) {
        Instant now = Instant.now();
        if(messages.get(0).getChat() == null) {
            UUID chatId = UUID.randomUUID();
            for(Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
                message.setChat(chatId);
            }
        }else{
            for(Message message : messages) {
                message.setId(UUID.randomUUID());
                message.setCreated(now);
            }
        }
    }

    private boolean isValid(List<Message> messages) {
        if(messages == null || messages.isEmpty()) {
            return false;
        }

        for(Message message : messages) {
            if (isEmpty(message.getSender())
                    || isEmpty(message.getReceiver())
                    || message.getType() == null
                    || isEmpty(message.getData())) {
                return false;
            }
        }

        Message oneFromAll = messages.get(0);

        if(oneFromAll.getChat() == null) {
            return hasTheSameSenderAndChatDoesntExist(messages, oneFromAll);
        } else {
            return hasTheSameSenderAndChat(messages, oneFromAll);
        }
    }

    private boolean hasTheSameSenderAndChat(List<Message> messages, Message oneFromAll) {
        return messages.stream().noneMatch(s -> !s.getSender().equals(oneFromAll.getSender()) || !s.getChat().equals(oneFromAll.getChat()));
    }

    private boolean hasTheSameSenderAndChatDoesntExist(List<Message> messages, Message oneFromAll) {
        return messages.stream().noneMatch(s -> !s.getSender().equals(oneFromAll.getSender()) || s.getChat() != null);
    }

    private boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    private boolean isEmpty(UUID uuid) {
        return uuid == null;
    }
}