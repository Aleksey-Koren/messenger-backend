package com.example.whisper.service.aop;

import com.example.whisper.entity.Administrator;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.service.impl.SecretMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final SecretMessageUtil secretMessageUtil;
    private final AdministratorRepository administratorRepository;

    public boolean hasRole(Map<String, String> headers, UUID chatId, Set<String> roles) {
        UUID senderUUID = UUID.fromString(headers.get("sender"));
        String encryptUUID = headers.get("token");
        String nonce = headers.get("nonce");

        UUID decryptUUID = UUID.fromString(secretMessageUtil.decryptSecretText(senderUUID, encryptUUID, nonce));
        if (senderUUID.compareTo(decryptUUID) > 0) {
            return false;
        }

        Administrator administrator = administratorRepository.findByUserIdAndChatId(senderUUID, chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User has no role in chat!"));

        return roles.contains(administrator.getUserType().name());
    }
}
