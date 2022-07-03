package com.example.whisper.controller;

import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @Value("${message.lifespan:86400000}")
    private Long messageLifespan;


    @PostMapping
    @Transactional
    public ResponseEntity<List<Message>> sendMessage(@RequestBody List<Message> messages, @RequestParam UUID iam) {
        return messageService.sendMessage(messages, iam);
    }

    @PutMapping("/title")
    @Transactional
    public List<Message> changeUserTitle(@RequestBody List<Message> messages, @RequestParam UUID iam) {
        return messageService.updateUserTitle(messages)
                .stream()
                .filter(message -> message.getSender().equals(iam))
                .collect(Collectors.toList());
    }

    @GetMapping()
    public Page<Message> getMessages(
            @RequestParam ("receiver") UUID receiver,
            @RequestParam(name = "created", required = false) Instant created,
            @RequestParam(name = "type", required = false) Message.MessageType type,
            @RequestParam(name = "chat", required = false) UUID chat,
            Pageable pageable
    ) {
        return messageRepository.findAll(((root, query, criteriaBuilder) -> {
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
            query.orderBy(criteriaBuilder.asc(root.get("created")));
            return where;
        }), pageable);
    }


    @Scheduled(fixedDelayString = "${message.lifespan:86400000}")
    public void deleteOld() {
        messageRepository.deleteAll(messageRepository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.lessThanOrEqualTo(
                                root.get("created"), Instant.now().minus(messageLifespan, ChronoUnit.MILLIS)),
                        criteriaBuilder.equal(root.get("type"), Message.MessageType.whisper)
        )));
    }
}
