package com.example.whisper.service;

import com.example.whisper.entity.Message;

import java.nio.file.Path;
import java.util.UUID;

public interface FileService {

    void deleteAllAttachments(Message message);

    String retrieveFolderPath(UUID messageId);

    void saveFiles(String folderPath, String[] files);

    void createDirectories(Path path);

}
