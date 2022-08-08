package com.example.whisper.controller;

import com.example.whisper.dto.AttachmentDto;
import com.example.whisper.service.AttachmentsService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<AttachmentDto> retrieveAttachments(@RequestParam UUID messageId) {
        return ResponseEntity.ok(attachmentsService.retrieveAttachments(messageId));
    }
}