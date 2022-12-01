package com.example.whisper.service.impl;

import com.example.whisper.entity.File;
import com.example.whisper.entity.Message;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.UtilMessageService;
import com.example.whisper.service.util.MessageHelperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UtilMessageServiceImpl implements UtilMessageService {

    private final MessageRepository messageRepository;
    private final FileServiceImpl fileService;

    public List<Message> processMessages(List<Message> messages) {
        MessageHelperUtil.setInstantData(messages);

        Message controlMessage = messages.get(0);
//        if (controlMessage.getAttachments() == null || "".equals(controlMessage.getAttachments())) {
//            return messageRepository.saveAll(messages);
//        } else {
//            return messageRepository.saveAll(processAttachments(messages));
//        }

        if (controlMessage.getFiles().size() == 0) {

            return messageRepository.saveAll(messages);
        } else {
            List<Message> messageList = processAttachments(messages);
            messages.forEach(message -> {
                AtomicInteger index = new AtomicInteger();
                message.getFiles().forEach(file -> {
                    file.setId(UUID.randomUUID());
                    file.setData(null);
                    file.setMessage(message);
                    file.setNumber(String.valueOf(index.get()));
                    index.getAndIncrement();
                });
            });

//            return messageRepository.saveAll(messages);

            return messageRepository.saveAll(messageList);

        }
    }

    private List<Message> processAttachments(List<Message> messages) {
        return messages.stream().map(this::processMessage).collect(Collectors.toList());
    }

    private Message processMessage(Message message) {
        String folderPath = fileService.retrieveFolderPath(message.getId());
//        String[] files = message.getAttachments().split(";");
        Set<File> files = message.getFiles();

        fileService.createDirectories(Paths.get(folderPath));
        fileService.saveFilesNew(folderPath, files);

        message.setAttachments(retrieveAttachmentsString(files));
        return message;
    }

    private String retrieveAttachmentsString(Set<File> files) {
        List<String> indexes = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            indexes.add(String.valueOf(i));
        }
        return String.join(";", indexes);
    }

}