package com.example.whisper.controller;

import com.example.whisper.service.AttachmentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("attachments")
@RequiredArgsConstructor
public class AttachmentsController {

    private final AttachmentsService attachmentsService;

    @GetMapping(produces = {MediaType.ALL_VALUE})
    public ResponseEntity<byte[]> retrieveAttachments(@RequestParam UUID messageId, @RequestParam String attachment) {
        return ResponseEntity.ok(attachmentsService.retrieveAttachment(messageId, attachment));
    }
}