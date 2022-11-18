package com.example.whisper.service.util;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.UtilRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.UUID;

import static javax.crypto.Cipher.DECRYPT_MODE;

/**
 * Class that responsible for decoding messages.
 *
 * @author Maksim Semianko
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DecoderUtil {

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

        return decrypt(secretText, sender.getPk(), secretKey.getUtilValue(), nonce);
    }

    private String decrypt(String input, String publicKeyPem, String privateKeyPem, String nonce) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] inputBytes = decoder.decode(input);

        PublicKey publicKeyToVerify = RsaKeyConverterUtil.pemToPublicKey(publicKeyPem);
        PrivateKey privateKeyToDecrypt = RsaKeyConverterUtil.pemToPrivateKey(privateKeyPem);

        verify(nonce, publicKeyToVerify);

        Cipher cipher = getCipherRSAForDecrypt(privateKeyToDecrypt);
        try {
            return new String(cipher.doFinal(inputBytes), StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method that verify nonce by public key.
     *
     * @param nonce     - hashed data
     * @param publicKey - for verify
     */
    private void verify(String nonce, PublicKey publicKey) {
        String[] values = nonce.split(":");
        Base64.Decoder decoder = Base64.getDecoder();

        byte[] signature = decoder.decode(values[0]);
        byte[] digest = decoder.decode(values[1]);

        try {
            Signature sign = Signature.getInstance("SHA1withRSA");
            sign.initVerify(publicKey);
            sign.update(digest);
            sign.verify(signature);

        } catch (SignatureException | InvalidKeyException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Cipher getCipherRSAForDecrypt(PrivateKey privateKey) {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(DECRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return cipher;
    }

}
