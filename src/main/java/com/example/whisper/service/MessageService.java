package com.example.whisper.service;

import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

        if(Message.MessageType.whisper.equals(messages.get(0).getType())) {
            messageRepository.saveAll(messages);
        }

        if(Message.MessageType.hello.equals(messages.get(0).getType())) {
            List<Message> helloMessages = messageRepository.findAllByChatAndType(messages.get(0).getChat(), Message.MessageType.hello);
            //todo deleteAllByChatAndType
            messageRepository.deleteAll(helloMessages);
            return ResponseEntity.ok(messageRepository.saveAll(messages));
        }

        if(Message.MessageType.iam.equals(messages.get(0).getType())) {
            UUID chat = messages.get(0).getChat();
            UUID sender = messages.get(0).getSender();
            messageRepository.deleteAllByChatAndSenderAndType(chat, sender, Message.MessageType.iam);
            return ResponseEntity.ok(messageRepository.saveAll(messages));
        }

        log.warn("Unknown type of message");
        return ResponseEntity.badRequest().build();
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