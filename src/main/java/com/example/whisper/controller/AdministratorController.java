package com.example.whisper.controller;

import com.example.whisper.dto.RequestRoleDto;
import com.example.whisper.entity.UserRole;
import com.example.whisper.service.UserRoleService;
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

    private final UserRoleService administratorService;

    @GetMapping("/chats/{chatId}")
    public List<UserRole> findAllByChatId(@PathVariable UUID chatId) {
        return administratorService.findAllByChatId(chatId);
    }

    @PostMapping("/customers/{customerId}/chats/{chatId}")
    @PreAuthorize("@securityService.hasRoleInChat(#token, #chatId, 'ADMINISTRATOR')")
    public ResponseEntity<UserRole> assignRole(@RequestHeader("Token") String token,
                                               @PathVariable UUID customerId,
                                               @PathVariable UUID chatId,
                                               @RequestBody RequestRoleDto request) {
        return new ResponseEntity<>(
                administratorService.createRoleByCustomerIdAndChatId(customerId, chatId, request), HttpStatus.CREATED);
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