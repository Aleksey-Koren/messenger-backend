package com.scnsoft.bot.logic.crypt;

import java.security.interfaces.RSAPrivateKey;

import javax.crypto.Cipher;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import com.scnsoft.bot.dto.MessageDto;
import com.scnsoft.bot.exception.MessageDecrypterException;
import com.scnsoft.bot.logic.CredentialMapper;

/**
 * Rsa message decryptor only (there is no need for rsa encryption in this context)
 * It is necessary to work on decrypting messages of the {@link com.scnsoft.bot.dto.MessageDto.MessageType.hello} type
 */
@Component
public record RsaAlgorithm(
    CredentialMapper credentialMapper
) {
    
    /**
     * Decrypts the encrypted 'hello' message that came from the messenger application to the bot.
     * @param messageDto  the encrypted 'hello' message from messenger app
     * @return  the decrypted incomming 'hello' message in bytes
     * @throws MessageDecrypterException If the bot credentials are specified incorrectly
     */
    public byte[] decrypt(MessageDto messageDto) throws MessageDecrypterException {
        try {
            RSAPrivateKey privateKey = credentialMapper.mapToRsaPrivateKey();

            byte[] inputBytes = Base64.decodeBase64(messageDto.data());
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(inputBytes);

        } catch (Exception e) {
            throw new MessageDecrypterException(e);
        }
    }
    
}
