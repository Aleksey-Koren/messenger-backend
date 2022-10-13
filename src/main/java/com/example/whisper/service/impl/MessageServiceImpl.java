package com.example.whisper.service.impl;

import com.example.whisper.app_properties.MessageProperties;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.ChatService;
import com.example.whisper.service.MessageService;
import com.example.whisper.service.validator.MessageValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UtilMessageServiceImpl whisperMessageService;
    private final MessageProperties messageProperties;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ServerMessageServiceImpl serverMessageService;
    private final ChatService chatService;

    @Override
    public Page<Message> findAllByParams(UUID receiver,
                                         Instant created,
                                         Instant before,
                                         Message.MessageType type,
                                         UUID chat,
                                         Pageable pageable) {
        return messageRepository.findAll((root, query, criteriaBuilder) -> {
            Predicate where = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("receiver"), receiver)
            );
            if (chat != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.equal(root.get("chat"), chat));
            }
            if (type != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.equal(root.get("type"), type));
            }
            if (created != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.greaterThanOrEqualTo(root.get("created"), created));
            }
            if (before != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.lessThan(root.get("created"), before));
            }
            query.orderBy(criteriaBuilder.desc(root.get("created")));
            return where;
        }, pageable);
    }

    @Override
    public void sendMessage(List<Message> messages) {
        System.out.println(messages);
        if (!MessageValidator.isValid(messages)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        List<Message> outMessages = whisperMessageService.processMessages(messages);

        for (Message message : outMessages) {
            simpMessagingTemplate.convertAndSendToUser(message.getReceiver().toString(), "/private", message);
        }
    }

    public void oldSendMessage(List<Message> messages, UUID iam) {
        if (!MessageValidator.isValid(messages)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        setInstantData(messages);

        Message controlMessage = messages.get(0);
        List<Message> out = new ArrayList<>();
        switch (controlMessage.getType()) {
            case whisper -> out = whisperMessageService.processMessages(messages);
            case who -> out = handleWhoMessages(messages, controlMessage);
            case hello -> out = handleHelloMessages(messages, controlMessage);
            case iam -> out = handleAimMessages(messages, controlMessage);
            case server -> {
                Message decrypted = serverMessageService.decryptServerMessage(messages);
                serverMessageService.processServerMessage(decrypted);
            }
            case LEAVE_CHAT -> out = messages;
            default -> {
                log.warn("Unknown type of message");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        for (Message message : out.stream().filter(message -> message.getReceiver().equals(iam)).toList()) {
            simpMessagingTemplate
                    .convertAndSendToUser(message.getReceiver().toString(), "/private", message);
        }
    }

    private List<Message> handleWhoMessages(List<Message> messages, Message controlMessage) {
        List<Message> foundedMessages = messageRepository.findByChatAndSenderAndReceiverAndType(
                controlMessage.getChat(),
                controlMessage.getSender(),
                controlMessage.getReceiver(),
                controlMessage.getType()
        );

        return foundedMessages.isEmpty() ? messageRepository.saveAll(messages) : new ArrayList<>();
    }

    private List<Message> handleHelloMessages(List<Message> messages, Message controlMessage) {
        List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
        if (!receivers.isEmpty()) {
            messageRepository.deleteHelloMessages(controlMessage.getChat(), Message.MessageType.hello, receivers);
        }
        if (controlMessage.getSender().equals(controlMessage.getReceiver())) {
            Chat chat = new Chat();
            chat.setId(controlMessage.getChat());
            chat.setCreatorId(controlMessage.getSender());
            chatService.create(chat);
        } else {
            chatService.addCustomerToChat(controlMessage.getReceiver(), controlMessage.getChat());
        }
        return messageRepository.saveAll(messages);
    }

    private List<Message> handleAimMessages(List<Message> messages, Message controlMessage) {
        List<UUID> receivers = messages.stream().map(Message::getReceiver).collect(Collectors.toList());

        messageRepository.deleteAllByChatAndSenderInAndReceiverAndType(
                controlMessage.getChat(),
                receivers,
                controlMessage.getSender(),
                Message.MessageType.who);

        List<Message> messageList = messageRepository.findByChatAndSenderAndReceiverAndType(
                controlMessage.getChat(),
                controlMessage.getSender(),
                controlMessage.getReceiver(), Message.MessageType.iam);

        return messageList.size() == 0 ? messageRepository.saveAll(messages) : messages;
    }

    public List<Message> findChats(UUID receiver) {
        List<Message> chats = messageRepository.findChats(receiver, Message.MessageType.hello);
        List<UUID> chatsIds = chats.stream().map(Message::getChat).toList();

//        Map<UUID, Instant> lastMessages = messageRepository.findLastMessageCreatedInChats(chatsIds)
//                .stream()
//                .collect(Collectors.toMap(LastMessageCreated::getChat, LastMessageCreated::getCreated));
//
//        chats.forEach(chat -> chat.setCreated(lastMessages.get(chat.getChat())));

        return chats;
    }

    public List<Message> updateUserTitle(List<Message> messages) {
        System.out.println("updateUserTitle");
        System.out.println(messages);
        if (!isValidUpdateTitle(messages)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Message commonData = messages.get(0);
        messageRepository.deleteAllBySenderAndType(commonData.getSender(), Message.MessageType.iam);
        setInstantData(messages);
        return messageRepository.saveAll(messages);
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

    private boolean isEmpty(UUID uuid) {
        return uuid == null;
    }

    private boolean isEmpty(String str) {
        return str == null || "".equals(str);
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

    @Override
    public List<Message> findOld() {
        return messageRepository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("created"), Instant.now().minus(messageProperties.getLifespan(), ChronoUnit.MILLIS)),
                        criteriaBuilder.equal(root.get("type"), Message.MessageType.whisper)
                ));
    }

    @Override
    public void deleteAll(List<Message> messages) {
        messageRepository.deleteAll(messages);
    }

}