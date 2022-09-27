package com.example.whisper.controller;

import com.example.whisper.dto.CreateRoleRequestDto;
import com.example.whisper.dto.DeleteRoleRequestDto;
import com.example.whisper.entity.Administrator;
import com.example.whisper.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @PostMapping("/role")
    public ResponseEntity<Administrator> createRole(@RequestBody CreateRoleRequestDto requestDto) {
        return new ResponseEntity<>(administratorService.createRole(
                requestDto.getCustomerId(),
                requestDto.getChatId(),
                requestDto.getRole(),
                requestDto.getAdministratorId()),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/role")
    public ResponseEntity<Void> deleteRole(@RequestBody DeleteRoleRequestDto requestDto) {
        administratorService.deleteRole(
                requestDto.getCustomerId(),
                requestDto.getChatId(),
                requestDto.getAdministratorId());
        return ResponseEntity.ok().build();
    }

}
