package com.example.whisper.service;

import com.example.whisper.entity.Message;
import com.example.whisper.exceptions.ServiceException;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WhisperMessageService {

    @Value("#{messageProperties.pathToAttachmentsFolder}")
    private String PATH_TO_ATTACHMENTS;

    private final MessageRepository messageRepository;

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
        String folderPath = preparePath(message.getId());

        try {
            Files.createDirectories(Paths.get(folderPath));
        } catch (IOException e) {
            String eMessage = "Troubles with folder creation";
            log.warn(eMessage);
            throw new ServiceException(eMessage, e);
        }

        String[] files = message.getAttachments().split(";");
        List<String> filePaths = saveFiles(folderPath, files);
        message.setAttachments(String.join(";", filePaths));
        return message;
    }

    private List<String> saveFiles(String folderPath, String[] files) {
        List<String> filePaths = new ArrayList<>();
        for(int i = 0; i < files.length; i++) {
            String filePath = folderPath + "/" + i + ".txt";
            filePaths.add(filePath);
            try {
                Files.writeString(Paths.get(filePath), files[i], StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                String eMessage = "Troubles with file creation";
                log.warn(eMessage);
                throw new ServiceException(eMessage, e);
            }
        }
        return filePaths;
    }

    private String preparePath(UUID messageId) {
        String id = messageId.toString();
        String separator = "/";
        StringBuilder absolutePath = new StringBuilder();

        absolutePath.append(PATH_TO_ATTACHMENTS);
        if(PATH_TO_ATTACHMENTS.charAt(PATH_TO_ATTACHMENTS.length() - 1) != '/') {
            absolutePath.append(separator);
        }
        absolutePath.append(id, 0, 4);
        absolutePath.append(separator);
        absolutePath.append(id, 4, 8);
        absolutePath.append(separator);
        absolutePath.append(id, 8 ,12);
        absolutePath.append(separator);
        absolutePath.append(id);

        return  absolutePath.toString();
    }

    public static void main(String[] args) {
        Path p = Paths.get("C://Koran");

    }
}