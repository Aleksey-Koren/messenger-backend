package com.example.whisper.service;

import com.example.whisper.entity.Message;
import com.example.whisper.exceptions.ServiceException;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhisperMessageService {

    private final MessageRepository messageRepository;
    private final FileService fileService;

    public List<Message> processMessages(List<Message> messages) {
        Message controlMessage = messages.get(0);
        if (controlMessage.getAttachments() == null || "".equals(controlMessage.getAttachments())) {
            return messageRepository.saveAll(messages);
        } else {
            return messageRepository.saveAll(processAttachments(messages));
        }
    }

    private List<Message> processAttachments(List<Message> messages) {
        return messages.stream().map(this::processMessage).collect(Collectors.toList());
    }

    private Message processMessage(Message message) {
        String folderPath = fileService.retrieveFolderPath(message.getId());
        fileService.createDirectories(Paths.get(folderPath));
        String[] files = message.getAttachments().split(";");
        fileService.saveFiles(folderPath, files);
        message.setAttachments(retrieveAttachmentsString(files));
        return message;
    }

    private String retrieveAttachmentsString(String[] files) {
        List<String> indexes = new ArrayList<>();
        for(int i = 0; i < files.length; i++) {
           indexes.add(String.valueOf(i));
        }
        return String.join(";", indexes);
    }
}