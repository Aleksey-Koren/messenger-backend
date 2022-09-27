package com.example.whisper.service.impl;

import com.example.whisper.app_properties.MessageProperties;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
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
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UtilMessageServiceImpl whisperMessageService;
    private final MessageProperties messageProperties;
    private final SimpMessagingTemplate simpMessagingTemplate;

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

    @Override
    public List<Message> findOld() {
        return messageRepository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("created"), Instant.now().minus(messageProperties.getLifespan(), ChronoUnit.MILLIS)),
                        criteriaBuilder.equal(root.get("type"), Message.MessageType.WHISPER)
                ));
    }

    @Override
    public void deleteAll(List<Message> messages) {
        messageRepository.deleteAll(messages);
    }

}