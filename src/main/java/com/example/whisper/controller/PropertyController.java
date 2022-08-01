package com.example.whisper.controller;

import com.example.whisper.dto.ClientPropertiesDto;
import com.example.whisper.service.UtilService;
import lombok.RequiredArgsConstructor;
import com.example.whisper.mapper.ClientPropertiesMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("properties")
@RequiredArgsConstructor
public class PropertyController {

    private final UtilService utilService;
    private final ClientPropertiesMapper clientPropertiesMapper;

    @GetMapping
    @Transactional
    public ResponseEntity<ClientPropertiesDto> getClientProperties() {
        ClientPropertiesDto dto = clientPropertiesMapper.toDto(utilService.findAll());
        return ResponseEntity.ok(dto);
    }
}