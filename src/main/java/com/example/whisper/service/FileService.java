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
        Path directory = Paths.get(retrieveFolderPath(message.getId()));
        deleteAllFiles(directory);
        deleteEmptyParents(directory);
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

    private void deleteAllFiles(Path directory) {
        if (Files.isDirectory(directory)) {
            try (Stream<Path> files = Files.list(directory)) {
                files.forEach(this::deleteFile);
            } catch (IOException e) {
                log.warn("Problem with reading files in attachment directory {}", absoluteToRelativePath(directory));
            }
        } else {
            log.warn("Retrieved pass {} doesn't point on directory. It isn't directory, or it doesn't exist", absoluteToRelativePath(directory));
        }
    }

    private void deleteEmptyParents(Path directory) {
        String rootDirectoryName = PATH_TO_ATTACHMENTS.substring(PATH_TO_ATTACHMENTS.lastIndexOf(SEPARATOR) + 1);
        while (!directory.getFileName().toString().equals(rootDirectoryName)) {
            if (isEmpty(directory)) {
                deleteDirectory(directory);
                directory = directory.getParent();
            } else {
                break;
            }
        }
    }


    private boolean isEmpty(Path directory) {
        try (Stream<Path> files = Files.list(directory)) {
            return files.findFirst().isEmpty();
        } catch (IOException e) {
            log.warn("Pass {} doesn't point on directory. It isn't directory, or it doesn't exist", absoluteToRelativePath(directory));
            return true;
        }
    }

    private void deleteFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.warn("File {} was not deleted. IOException", absoluteToRelativePath(file));
        } catch (SecurityException e) {
            log.warn("File {} was not deleted. Troubles with file access", absoluteToRelativePath(file));
        }
    }

    private void deleteDirectory(Path directory) {
        try {
            Files.deleteIfExists(directory);
        } catch (IOException e) {
            log.warn("Directory {} was not deleted. IOException", absoluteToRelativePath(directory));
        } catch (SecurityException e) {
            log.warn("Directory {} was not deleted. Troubles with file access", absoluteToRelativePath(directory));
        }
    }

    private String absoluteToRelativePath(Path filePath) {
        return filePath.toString().substring(PATH_TO_ATTACHMENTS.length());
    }
}