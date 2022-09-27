package com.example.whisper.service;

import com.example.whisper.dto.FileStreamDto;

import java.util.UUID;

public interface AttachmentsService {

    FileStreamDto retrieveFileStream(String attachment, UUID messageId);
}
