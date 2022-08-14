package com.example.whisper.service;
import com.example.whisper.dto.FileStreamDto;
import com.example.whisper.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttachmentsService {

   private final WhisperMessageService whisperMessageService;

   public FileStreamDto retrieaveFileStream(String attachment, UUID messageId) {
      String separator = FileSystems.getDefault().getSeparator();

      String pathToFolder = whisperMessageService.retrieveFolderPath(messageId);
      String pathToFile = pathToFolder + separator + attachment;
      InputStream inputStream;
      long contentLength;
         try {
            Path path = Paths.get(pathToFile);
            inputStream = Files.newInputStream(Paths.get(pathToFile));
            contentLength = Files.size(path);
            Files.newInputStream(path);
         } catch (IOException e) {
            throw new ServiceException("Troubles with file reading", e);
         }
      return FileStreamDto.builder().inputStream(inputStream).contentLength(contentLength).build();
   }
}