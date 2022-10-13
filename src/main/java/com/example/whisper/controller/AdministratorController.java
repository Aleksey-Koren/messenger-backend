package com.example.whisper.controller;

import com.example.whisper.dto.RequestRoleDto;
import com.example.whisper.entity.Administrator;
import com.example.whisper.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @GetMapping("/")
    public ResponseEntity<String> get() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("@securityService.hasRole(#headers, #roleDto.chatId, 'ADMINISTRATOR,MODERATOR')")
    public ResponseEntity<Administrator> assignRole(@RequestHeader Map<String, String> headers,
                                                    @RequestBody RequestRoleDto roleDto) {
        System.out.println("assignRole");
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @DeleteMapping("/customers/{customerId}/chats/{chatId}")
    public ResponseEntity<Administrator> denyRole(@PathVariable String customerId, @PathVariable String chatId) {
        return new ResponseEntity<>
                (null,
                        HttpStatus.OK);
    }

}