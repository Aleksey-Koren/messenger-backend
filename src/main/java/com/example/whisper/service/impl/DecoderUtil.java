package com.example.whisper.service.impl;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.UtilRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecoderUtil {

    private final CryptServiceImpl cryptService;
    private final UtilRepository utilRepository;
    private final CustomerRepository customerRepository;

    public String decryptToken(String token) {
        String[] parsedToken = token.split("_");

        if (parsedToken.length != 3) {
            log.error("Invalid token!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token!");
        }

        String secretText = parsedToken[0];
        String nonce = parsedToken[1];
        UUID senderId = UUID.fromString(parsedToken[2]);

        return decryptSecretText(senderId, secretText, nonce);
    }

    public String decryptToken(String token, UUID senderId) {
        String[] parsedToken = token.split("_");

        if (parsedToken.length != 3) {
            log.error("Invalid token!");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token!");
        }

        String secretText = parsedToken[0];
        String nonce = parsedToken[1];

        return decryptSecretText(senderId, secretText, nonce);
    }

    public String decryptSecretText(UUID senderId, String secretText, String nonce) {
        Customer sender = customerRepository.findById(senderId).orElseThrow(() -> {
            log.warn("Sender with id = {} doesn't exist in database", senderId);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST);
        });

        Utility secretKey = utilRepository.findById(Utility.Key.SERVER_USER_SECRET_KEY.name()).orElseThrow(() -> {
            log.warn("Server user secret key doesn't exist in database");
            return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        });

        Base64.Decoder decoder = Base64.getDecoder();

        try {
            decrypt(Arrays.toString(decoder.decode(secretText)), sender.getPk(), secretKey.getUtilValue(), nonce);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return cryptService.decrypt(decoder.decode(secretText),
                decoder.decode(sender.getPk()),
                decoder.decode(nonce),
                decoder.decode(secretKey.getUtilValue()));
    }

    private String decrypt(String input, String publicKeyToVerify, String secretKeyToDecrypt, String nonce) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPEM = publicKeyToVerify
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        System.out.println(publicKey);

        return null;
    }


}
