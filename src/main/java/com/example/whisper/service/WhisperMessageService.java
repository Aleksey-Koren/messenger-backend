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
import java.nio.file.FileSystems;
import java.nio.file.Files;
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

    private final String SEPARATOR = FileSystems.getDefault().getSeparator();

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
        String folderPath = retrieveFolderPath(message.getId());

        try {
            Files.createDirectories(Paths.get(folderPath));
        } catch (IOException e) {
            String eMessage = "Troubles with folder creation";
            log.warn(eMessage);
            throw new ServiceException(eMessage, e);
        }

        String[] files = message.getAttachments().split(";");
        saveFiles(folderPath, files);
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

    private void saveFiles(String folderPath, String[] files) {
        for(int i = 0; i < files.length; i++) {
            String filePath = folderPath + SEPARATOR + i;
            try {
                Files.writeString(Paths.get(filePath), files[i], StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                String eMessage = "Troubles with file creation";
                log.warn(eMessage);
                throw new ServiceException(eMessage, e);
            }
        }
    }

    public String retrieveFolderPath(UUID messageId) {
        String id = messageId.toString();
        StringBuilder absolutePath = new StringBuilder();

        absolutePath.append(PATH_TO_ATTACHMENTS);

        char lastChar = PATH_TO_ATTACHMENTS.charAt(PATH_TO_ATTACHMENTS.length() - 1);
        char separator = SEPARATOR.toCharArray()[0];
        if(lastChar != separator) {
            absolutePath.append(SEPARATOR);
        }

        absolutePath.append(id, 0, 3);
        absolutePath.append(SEPARATOR);
        absolutePath.append(id, 3, 6);
        absolutePath.append(SEPARATOR);
        absolutePath.append(id, 6 ,8);
        absolutePath.append(SEPARATOR);
        absolutePath.append(id);

        return  absolutePath.toString();
    }
}