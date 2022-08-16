package com.example.whisper.service;

import com.example.whisper.entity.Message;
import com.example.whisper.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileService {

    private final String PATH_TO_ATTACHMENTS;
    private final String SEPARATOR;

    public FileService(
            @Value("#{messageProperties.pathToAttachmentsFolder}") String PATH_TO_ATTACHMENTS
    ) {
        this.PATH_TO_ATTACHMENTS = PATH_TO_ATTACHMENTS;
        this.SEPARATOR = FileSystems.getDefault().getSeparator();
    }

    public void deleteAllAttachments(Message message) {
        Path directoryPath = Paths.get(retrieveFolderPath(message.getId()));
        deleteAllFilesWithDirectory(directoryPath);
    }

    public String retrieveFolderPath(UUID messageId) {
        String id = messageId.toString();
        StringBuilder absolutePath = new StringBuilder();

        absolutePath.append(PATH_TO_ATTACHMENTS);

        char lastChar = PATH_TO_ATTACHMENTS.charAt(PATH_TO_ATTACHMENTS.length() - 1);
        char separator = SEPARATOR.toCharArray()[0];
        if (lastChar != separator) {
            absolutePath.append(SEPARATOR);
        }

        absolutePath.append(id, 0, 3);
        absolutePath.append(SEPARATOR);
        absolutePath.append(id, 3, 6);
        absolutePath.append(SEPARATOR);
        absolutePath.append(id, 6, 8);
        absolutePath.append(id, 9, 10);
        absolutePath.append(SEPARATOR);
        absolutePath.append(id);

        return absolutePath.toString();
    }

    public void saveFiles(String folderPath, String[] files) {
        List<byte[]> filesAsBytes = Arrays.stream(files).map(s -> Base64.getDecoder().decode(s)).collect(Collectors.toList());
        for (byte[] arr : filesAsBytes) {
            String filePath = folderPath + SEPARATOR + filesAsBytes.indexOf(arr);
            try {
                Files.write(Paths.get(filePath), arr, StandardOpenOption.CREATE_NEW);
            } catch (IOException e) {
                String eMessage = "Troubles with file creation";
                log.warn(eMessage);
                throw new ServiceException(eMessage, e);
            }
        }
    }

    public void createDirectories(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            String eMessage = "Troubles with folder creation";
            log.warn(eMessage);
            throw new ServiceException(eMessage, e);
        }
    }

    private void deleteAllFilesWithDirectory(Path path) {
        assertDirectory(path);
        try (Stream<Path> list = Files.list(path)) {
            final List<Path> paths = list.toList();
            for (Path filePath : paths) {
                Files.deleteIfExists(filePath);
            }
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new ServiceException(e);
        }
        log.info("Attachments in folder {} has been deleted with the folder", path.getFileName());
        System.out.println(path.getFileName());
    }

    private void assertDirectory(Path directory) {
        if(!Files.isDirectory(directory)) {
            String message = "Path witch was retrieved from message id is not a directory";
            log.warn(message);
            throw new ServiceException(message);
        }
    }

//    private boolean isEmpty(Path directory) {
//        try(Stream<Path> files = Files.list(directory)) {
//            return files.findFirst().isEmpty();
//        }catch (IOException e) {
//            throw new ServiceException(e);
//        }
//    }

//    public boolean deleteDirectoryIfIsEmpty(Path path) {
//        assertDirectory(path);
//        if(isEmpty(path)) {
//            try {
//                Files.delete(path);
//                return true;
//            } catch (IOException e) {
//                throw new ServiceException(e);
//            }
//        } else {
//            return false;
//        }
//    }
}