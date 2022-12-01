package com.example.whisper.service.impl;

import com.example.whisper.app.properties.MessageProperties;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.ChatService;
import com.example.whisper.service.MessageService;
import com.example.whisper.service.util.MessageHelperUtil;
import com.example.whisper.service.validator.CustomerValidator;
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

    private final ChatService chatService;
    private final UtilMessageServiceImpl whisperMessageService;
    private final MessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final MessageProperties messageProperties;

    @Override
    public Page<Message> findAllByParams(UUID receiver,
                                         Instant created,
                                         Instant before,
                                         Message.MessageType type,
                                         UUID chat,
                                         Pageable pageable) {
        return messageRepository.findAll((root, query, criteriaBuilder) -> {
            Predicate where = criteriaBuilder.and(criteriaBuilder.equal(root.get(Message.Fields.receiver), receiver));
            if (chat != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.equal(root.get(Message.Fields.chat), chat));
            }
            if (type != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.equal(root.get(Message.Fields.type), type));
            }
            if (created != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.greaterThanOrEqualTo(root.get(Message.Fields.created), created));
            }
            if (before != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.lessThan(root.get(Message.Fields.created), before));
            }
            query.orderBy(criteriaBuilder.desc(root.get(Message.Fields.created)));
            return where;
        }, pageable);
    }

    @Override
    public void sendMessage(List<Message> messages) {
        if (MessageValidator.isNotValid(messages)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        MessageHelperUtil.setInstantData(messages);

        Message controlMessage = messages.get(0);
        List<Message> out;

        switch (controlMessage.getType()) {
            case WHISPER -> out = whisperMessageService.processMessages(messages);
            case WHO -> out = handleWhoMessages(messages, controlMessage);
            case HELLO -> out = handleHelloMessages(messages, controlMessage);
            case IAM -> out = handleAimMessages(messages, controlMessage);
            case LEAVE_CHAT -> out = messages;
            default -> {
                log.warn("Unknown type of message");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }

        for (Message message : out) {
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
            messageRepository.deleteHelloMessages(controlMessage.getChat(), Message.MessageType.HELLO, receivers);
        }
        if (controlMessage.getSender().equals(controlMessage.getReceiver())) {
            Chat chat = new Chat();
            UUID chatId = UUID.randomUUID();

            chat.setId(chatId);
            chat.setCreatorId(controlMessage.getSender());

            controlMessage.setChat(chatId);
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
                Message.MessageType.WHO);

        List<Message> messageList = messageRepository.findByChatAndSenderAndReceiverAndType(
                controlMessage.getChat(),
                controlMessage.getSender(),
                controlMessage.getReceiver(), Message.MessageType.IAM);

        return messageList.size() == 0 ? messageRepository.saveAll(messages) : messages;
    }

    @Override
    public List<Message> getChatsByReceiverId(UUID receiver) {
        return messageRepository.findChats(receiver, Message.MessageType.HELLO);
    }

    public List<Message> updateUserTitle(List<Message> messages) {
        if (!CustomerValidator.isValidUpdateTitle(messages)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Message commonData = messages.get(0);
        messageRepository.deleteAllBySenderAndType(commonData.getSender(), Message.MessageType.IAM);
        MessageHelperUtil.setInstantData(messages);
        return messageRepository.saveAll(messages);
    }

    @Override
    public List<Message> findOld() {
        return messageRepository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get(Message.Fields.created), Instant.now().minus(messageProperties.getLifespan(),
                                        ChronoUnit.MILLIS)),
                        criteriaBuilder.equal(root.get(Message.Fields.type), Message.MessageType.WHISPER)
                ));
    }

    @Override
    public List<Customer> getParticipantsByCharId(UUID chatId) {
        List<Message> messages = messageRepository.findAllByChatAndType(chatId, Message.MessageType.HELLO);
        List<UUID> participants = messages.stream().map(Message::getReceiver).collect(Collectors.toList());
        if (participants.isEmpty()) {
            return new ArrayList<>();
        } else {
            return customerRepository.findAllById(participants);
        }
    }

    @Override
    public void deleteAll(List<Message> messages) {
        messageRepository.deleteAll(messages);
    }

}