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

   public byte[] retrieveAttachment(UUID messageId, String attachment) {
      return readFile(attachment, messageId);
   }

   private byte[] readFile (String attachment, UUID messageId) {
      String separator = FileSystems.getDefault().getSeparator();
      byte[] file;

         String pathToFolder = whisperMessageService.retrieveFolderPath(messageId);
         String pathToFile = pathToFolder + separator + attachment;
         try {
            file = Files.readAllBytes(Paths.get(pathToFile));
         } catch (IOException e) {
            throw new ServiceException("Troubles with file reading", e);
         }
      return file;
   }
}