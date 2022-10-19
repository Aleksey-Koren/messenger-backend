package com.example.whisper.service.aop;

import com.example.whisper.entity.Administrator;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.service.impl.SecretMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final SecretMessageUtil secretMessageUtil;
    private final AdministratorRepository administratorRepository;

    public boolean hasRoleInChat(String token, UUID chatId, Set<String> roles) {
        String[] parsedToken = token.split("_");

        if (parsedToken.length != 3) {
            log.error("Invalid token!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token!");
        }

        String encryptUUID = parsedToken[0];
        String nonce = parsedToken[1];
        UUID senderUUID = UUID.fromString(parsedToken[2]);

        UUID decryptUUID = UUID.fromString(secretMessageUtil.decryptSecretText(senderUUID, encryptUUID, nonce));
        if (senderUUID.compareTo(decryptUUID) > 0) {
            return false;
        }

        Administrator administrator = administratorRepository.findByUserIdAndChatId(senderUUID, chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User has no role in chat!"));

        return roles.contains(administrator.getUserType().name());
    }
}
