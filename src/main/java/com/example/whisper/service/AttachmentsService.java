package com.example.whisper.service;

import com.example.whisper.dto.AttachmentDto;
import com.example.whisper.entity.Message;
import com.example.whisper.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentsService {

   private final MessageService messageService;
   private final WhisperMessageService whisperMessageService;

   public AttachmentDto retrieveAttachments(UUID messageId) {
      Message message = messageService.getById(messageId);
      List<String> files = readFiles(message.getAttachments().split(";"), messageId);
      return  new AttachmentDto(files);
   }

   private List<String> readFiles(String[] attachments, UUID messageId) {
      List<String> files = new ArrayList<>();
      String separator = FileSystems.getDefault().getSeparator();
      String file;

      for (String attachment : attachments) {
         String pathToFolder = whisperMessageService.retrieveFolderPath(messageId);
         String pathToFile = pathToFolder + separator + attachment;
         try {
            file = Files.readString(Paths.get(pathToFile));
         } catch (IOException e) {
            throw new ServiceException("Troubles with file reading", e);
         }
         files.add(file);
      }

      return files;
   }
}