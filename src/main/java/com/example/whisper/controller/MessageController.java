package com.example.whisper.controller;

import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Value("${message.lifespan:86400000}")
    private Long messageLifespan;


    @PutMapping
    public ResponseEntity<List<Message>> sendMessage(@RequestBody List<Message> messages) {
        List<Message> out = new ArrayList<>();
        for(Message message : messages) {
            message.setId(UUID.randomUUID());
            message.setCreated(Instant.now());
            if (isEmpty(message.getSender())
                    || isEmpty(message.getReceiver())
                    || message.getType() == null
                    || isEmpty(message.getData())) {
                return ResponseEntity.badRequest().build();
            }
            if (message.getChat() == null) {
                message.setChat(UUID.randomUUID());
            }
            out.add(messageRepository.save(message));
        }
        return ResponseEntity.ok(out);
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

            if(created != null) {
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



    private boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }
    private boolean isEmpty(UUID uuid) {
        return uuid == null;
    }

}
