package com.example.whisper.controller;

import com.example.whisper.dto.FileStreamDto;
import com.example.whisper.service.AttachmentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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

    @GetMapping(produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<InputStreamResource> retrieveAttachments(@RequestParam UUID messageId, @RequestParam String attachment) {
        FileStreamDto dto = attachmentsService.retrieaveFileStream(attachment, messageId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(dto.getContentLength());
        InputStreamResource inputStreamResource = new InputStreamResource(dto.getInputStream());
        return ResponseEntity.ok().headers(headers).body(inputStreamResource);
    }
}