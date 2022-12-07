package com.scnsoft.bot.logic.crypt;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.scnsoft.bot.dto.MessageDto;
import com.scnsoft.bot.exception.MessageDecrypterException;
import com.scnsoft.bot.exception.MessageEncrypterException;

/**
 * Aes message decryptor/encryptor
 * It is necessary to work on decrypting/encrypting messages of the {@link com.scnsoft.bot.dto.MessageDto.MessageType.whisper} type
 */
@Component
public record AesAlgorithm(
    RsaAlgorithm rsaAlgorithm,
    RestTemplate restTemplate
) {
        
    private static final byte UNDERSCORE_UTF16_BYTE_NUMBER = 95;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private static final String MESSENGER_URL = "http://localhost:8080";

    
    /**
     * Encryptes the decrypted 'whisper' message that sends to the messenger application from the bot
     * @param messageDto  the decrypted 'whisper' message from the bot app
     * @return  the encrypted 'whisper' message from the bot app in bytes
     * @throws MessageEncrypterException  If the bot data is specified incorrectly
     */
    public byte[] encrypt(MessageDto messageDto) throws MessageEncrypterException {
        try {
            String nonce = messageDto.nonce();
            SecretKey aesKey = getAesSecretKey(messageDto.chat(), messageDto.sender());
            IvParameterSpec ivParameterSpec = getInitializationVector(nonce);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivParameterSpec);
            String messageData = messageDto.data();

            byte[] cipherText = cipher.doFinal(messageData.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64(cipherText);
        } catch (Exception e) {
            throw new MessageEncrypterException(e);
        }
    }

    /**
     * Decrypts the encrypted 'whisper' message that came from the messenger application to the bot.
     * @param messageDto  the encrypted 'whisper' message from messenger app
     * @return  the decrypted incomming 'whisper' message from the messenger in bytes
     * @throws MessageDecrypterException  If the bot data is specified incorrectly
     */
    public byte[] decrypt(MessageDto messageDto) throws MessageDecrypterException {
        try {
            String nonce = messageDto.nonce();
            SecretKey aesKey = getAesSecretKey(messageDto.chat(), messageDto.receiver());
            IvParameterSpec ivParameterSpec = getInitializationVector(nonce);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, aesKey, ivParameterSpec);

            byte[] decodedMessageData = Base64.decodeBase64(messageDto.data());
            return cipher.doFinal(decodedMessageData);
            
        } catch (Exception e) {
            throw new MessageDecrypterException(e);
        }
    }

    
    /**
     * Generates nonce with given length
     * @param size  the nonce length
     * @return  the generated nonce string
     */
    public String generateNonce(int size) {
        byte[] nonce = new byte[size];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(nonce);
        return Base64.encodeBase64String(nonce);
    }
    

    private IvParameterSpec getInitializationVector(String nonce) {
        byte[] initializationVector = Base64.decodeBase64(nonce);
        return new IvParameterSpec(initializationVector);
    }


    private SecretKey getAesSecretKey(UUID chat, UUID receiver) throws MessageDecrypterException {
        String helloMessagesUrl = MESSENGER_URL + "/messages/hello?chat=" + chat + "&receiver=" + receiver;
        ResponseEntity<MessageDto> response = restTemplate.getForEntity(helloMessagesUrl, MessageDto.class);   
        MessageDto helloMessageDto = response.getBody(); 
        byte[] decryptedRsaBytes = rsaAlgorithm.decrypt(helloMessageDto);
        int underscoreIndex = getDoubleUnderscoreIndex(decryptedRsaBytes);
        byte[] key = Arrays.copyOfRange(decryptedRsaBytes, underscoreIndex + 1, decryptedRsaBytes.length);
        return new SecretKeySpec(key, "AES");
    }


    private int getDoubleUnderscoreIndex(byte[] decryptedRsaBytesArray) {
        boolean isPreviousSymbolUnderscore = false;
        for (int i = 0; i < decryptedRsaBytesArray.length; i++) {
            if (decryptedRsaBytesArray[i] == UNDERSCORE_UTF16_BYTE_NUMBER) {
                if (isPreviousSymbolUnderscore) {
                    return i;
                } else {
                    isPreviousSymbolUnderscore = true;
                }
            }
        }

        return -1;
    }
}
