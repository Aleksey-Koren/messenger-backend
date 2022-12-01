package com.example.whisper.controller;

import com.example.whisper.dto.FileDto;
import com.example.whisper.entity.File;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("files")
@RequiredArgsConstructor
public class FileController {

    private final FileRepository fileRepository;

    @GetMapping()
    public ResponseEntity<FileDto> getFile(@RequestParam UUID messageId, @RequestParam String attachment) {
        File file = fileRepository
                .findByMessageIdAndNumber(messageId, attachment)
                .orElseThrow(() -> new ResourseNotFoundException("File not found!"));

        FileDto fileDto = new FileDto();
        fileDto.setName(file.getName());
        fileDto.setType(file.getType());

        return ResponseEntity.ok().body(fileDto);
    }
}