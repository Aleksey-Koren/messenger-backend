package com.example.whisper.service.impl;

import com.example.whisper.service.CryptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

import static com.iwebpp.crypto.TweetNaclFast.Box;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptServiceImpl implements CryptService {

    @Override
    public String decrypt(byte[] input, byte[] publicKeyToVerify, byte[] nonce, byte[] secretKeyToDecrypt) {
        Box box = new Box(publicKeyToVerify, secretKeyToDecrypt);
        byte[] decrypted = box.open(input, nonce);
        if (decrypted == null) {
            log.warn("Message hasn't been decrypted");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}