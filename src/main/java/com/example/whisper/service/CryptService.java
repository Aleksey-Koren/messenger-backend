package com.example.whisper.service;

import com.example.whisper.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

import static com.iwebpp.crypto.TweetNaclFast.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptService {

    public String decrypt(byte[] input, byte[] publicKeyToVerify, byte[] nonce, byte[] secretKeyToDecrypt) {
        Box box = new Box(publicKeyToVerify, secretKeyToDecrypt);
        byte[] decrypted = box.open(input, nonce);
        if(decrypted == null) {
            log.warn("Message hasn't been decrypted");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}