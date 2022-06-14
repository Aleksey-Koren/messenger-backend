package com.example.whisper.controller;

import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OrderBy;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<List<Message>> sendMessage(@RequestBody List<Message> messages) {
        return messageService.sendMessage(messages);
    }

    @PutMapping("/title")
    @Transactional
    public void changeUserTitle(@RequestBody List<Message> messages) {
        messageService.updateUserTitle(messages);
    }

    @GetMapping()
    public List<Message> getMessages(
            @RequestParam ("receiver") UUID receiver,
            @RequestParam(name = "created", required = false) Instant created,
            @RequestParam(name = "chat", required = false) UUID chat
    ) {

        return messageRepository.findAll(((root, query, criteriaBuilder) -> {
            Predicate where = criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("receiver"), receiver),
                    criteriaBuilder.equal(root.get("chat"), chat)
            );

            if (created != null) {
                where = criteriaBuilder.and(where, criteriaBuilder.greaterThanOrEqualTo(root.get("created"), created));
            }
            query.orderBy(criteriaBuilder.asc(root.get("created")));
            return where;
        }));
    }


    @Scheduled(fixedDelayString = "${message.lifespan:86400000}")
    public void deleteOld() {
        messageRepository.deleteAll(messageRepository.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(root.get("created"),
                        Instant.now().minus(messageLifespan, ChronoUnit.MILLIS)
        )));
    }
}
