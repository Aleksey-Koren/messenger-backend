package com.example.whisper.controller;

import com.example.whisper.entity.Message;
import com.example.whisper.service.impl.FileServiceImpl;
import com.example.whisper.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageServiceImpl messageService;
    private final FileServiceImpl fileService;


    // DELETE LATER
    @PostMapping
    @Transactional
    public void sendMessage(@RequestBody List<Message> messages, @RequestParam UUID iam) {
        messageService.sendMessage(messages);
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
    public ResponseEntity<Page<Message>> getMessages(
            @RequestParam("receiver") UUID receiver,
            @RequestParam(name = "created", required = false) Instant created,
            @RequestParam(name = "before", required = false) Instant before,
            @RequestParam(name = "type", required = false) Message.MessageType type,
            @RequestParam(name = "chat", required = false) UUID chat,
            Pageable pageable
    ) {
        return new ResponseEntity<>(
                messageService.findAllByParams(receiver, created, before, type, chat, pageable),
                HttpStatus.OK);
    }

    @Scheduled(fixedDelayString = "#{@messageProperties.getLifespan()}")
    public void deleteOld() {
        List<Message> old = messageService.findOld();
        old.stream().filter(message -> message.getAttachments() != null).forEach(fileService::deleteAllAttachments);
        messageService.deleteAll(old);
    }
}
