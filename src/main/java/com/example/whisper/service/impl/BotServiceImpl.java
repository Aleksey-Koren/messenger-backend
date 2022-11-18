package com.example.whisper.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.whisper.entity.Bot;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.BotRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.BotService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotServiceImpl implements BotService {

    private final BotRepository botRepository;
    private final MessageRepository messageRepository;

    @Override
    public List<Bot> findAllByIds(List<UUID> ids) {
        return botRepository.findAllById(ids);
    }

    @Override
    public Bot findById(UUID id) {
        return botRepository.findById(id).orElseThrow(() -> {
            log.warn("No bot with id = {} in database", id);
            throw new ResourseNotFoundException("Bot not found by id!");
        });
    }

    @Override
    public Bot register(Bot bot) {
        bot.setId(UUID.randomUUID());
        return botRepository.save(bot);
    }
    
    @Override
    public Void delete(Bot bot) {
        Bot botToDelete = findById(bot.getId());

        try {
            byte[] bytes = Base64.getDecoder().decode(bot.getPk());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateFromPublic =
                    new X509EncodedKeySpec(botToDelete.getPk().getBytes(StandardCharsets.UTF_8));
            PrivateKey pseudoPrivateKey = keyFactory.generatePrivate(privateFromPublic);

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, pseudoPrivateKey);

            byte[] decryptedMessageBytes = decryptCipher.doFinal(bytes);
            UUID decryptedMessage = UUID.fromString(new String(decryptedMessageBytes, StandardCharsets.UTF_8));
            if (decryptedMessage.equals(botToDelete.getId())) {
                messageRepository.deleteMyMessages(bot.getId());
                botRepository.delete(botToDelete);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("Crypto error", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            log.error("invalid data in delete bot");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return null;
    }

}
