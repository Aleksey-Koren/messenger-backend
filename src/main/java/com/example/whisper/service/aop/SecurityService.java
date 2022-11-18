package com.example.whisper.service.aop;

import com.example.whisper.entity.UserRole;
import com.example.whisper.repository.UserRoleRepository;
import com.example.whisper.service.util.DecoderUtil;
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

    private final DecoderUtil decoderUtil;
    private final UserRoleRepository administratorRepository;

    public boolean isOwner(String token, UUID ownerId) {
        UUID decryptUUID = UUID.fromString(decoderUtil.decryptToken(token, ownerId));
        return ownerId.compareTo(decryptUUID) == 0;
    }

    /**
     * Method that check that user has role in chat.
     *
     * @param token  contains encryptUUID + nonce + senderUUID
     * @param chatId desired chat for checking role in him
     * @param roles  permission for method
     * @return true or false
     */
    public boolean hasRoleInChat(String token, UUID chatId, Set<String> roles) {
        if (!isValidToken(token)) {
            return false;
        }

        UserRole userRole = administratorRepository
                .findByUserIdAndChatId(getSenderIdFromToken(token), chatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User has no role in chat!"));

        return roles.contains(userRole.getUserType().name());
    }

    private boolean isValidToken(String token) {
        UUID decryptUUID = UUID.fromString(decoderUtil.decryptToken(token));
        UUID senderUUID = getSenderIdFromToken(token);

        return senderUUID.compareTo(decryptUUID) == 0;
    }

    private UUID getSenderIdFromToken(String token) {
        return UUID.fromString(token.split("_")[2]);
    }

}
