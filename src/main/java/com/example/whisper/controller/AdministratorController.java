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

import java.util.List;
import java.util.UUID;

/**
 * @author Maksim Semianko
 */
@RestController
@RequestMapping("administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

    @GetMapping("/chats/{chatId}")
    public ResponseEntity<List<Administrator>> findAllByChatId(@PathVariable UUID chatId) {
        return new ResponseEntity<>(administratorService.findAllByChatId(chatId), HttpStatus.OK);
    }

    @PostMapping("/")
    @PreAuthorize("@securityService.hasRoleInChat(#token, #roleDto.chatId, 'ADMINISTRATOR')")
    public ResponseEntity<Administrator> assignRole(@RequestHeader("Token") String token,
                                                    @RequestBody RequestRoleDto roleDto) {
        return new ResponseEntity<>(administratorService.createRoleByCustomerIdAndChatId(roleDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/customers/{customerId}/chats/{chatId}")
    @PreAuthorize("@securityService.hasRoleInChat(#token, #chatId, 'ADMINISTRATOR')")
    public ResponseEntity<Void> denyRole(@RequestHeader("Token") String token,
                                         @PathVariable UUID customerId,
                                         @PathVariable UUID chatId) {
        administratorService.deleteRoleByCustomerIdAndChatId(customerId, chatId);
        return ResponseEntity.ok().build();
    }

}