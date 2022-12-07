package com.scnsoft.bot.logic;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import com.scnsoft.bot.exception.InvalidBotCredentialsException;

/**
 * Mapper of the string credentials received from the messenger bot bean into objects 
 * of the classes necessary for encryption and decryption of messages
 */
@Component
public record CredentialMapper(MessengerBot messengerBot) {
    public CredentialMapper{
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ALGORITHM = "RSA";

    private static final String RSA_PRIVATE_KEY_REGEX = "(?<=-----BEGIN RSA PRIVATE KEY-----(\\r\\n?|\\n))((.*)\\1)+";

    private static final String RSA_PUBLIC_KEY_REGEX = "(?<=-----BEGIN PUBLIC KEY-----(\\r\\n?|\\n))((.*)\\1)+";

    private static final String SPACE_REGEX = "\\p{Blank}";

    /**
     * Maps bots ID of String type to UUID
     * @return  the bot id of the {@link java.util.UUID} type
     */
    public UUID mapToUUID() {
        String uuidString = messengerBot.getBotId();
        uuidString = uuidString.replaceAll(SPACE_REGEX, "");
        return UUID.fromString(messengerBot.getBotId());
    }
    

    /**
     * Maps bot private key in PKCS#1 format of Strign type to {@link java.security.interfaces.RSAPrivateKey} type
     * @return  the rsa private key
     * @throws InvalidBotCredentialsException  If the private key string has the wrong format 
     *                                         or the algorithm is set incorrectly
     */
    public RSAPrivateKey mapToRsaPrivateKey() throws InvalidBotCredentialsException {
        try {
            Pattern pattern = Pattern.compile(RSA_PRIVATE_KEY_REGEX);
            String privateKeyStringWithoutHeaderAndFooter = pattern
                .matcher(messengerBot.getPrivateKey())
                .results()
                .findFirst()
                .map(MatchResult::group)
                .orElseThrow(InvalidBotCredentialsException::new);

            String privateKeyStringWithoutLineSeparators = privateKeyStringWithoutHeaderAndFooter
                .replaceAll(System.lineSeparator(), "")
                .replaceAll(SPACE_REGEX, "");

            byte[] decodedPrivateKey = Base64.decodeBase64(privateKeyStringWithoutLineSeparators);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InvalidBotCredentialsException(e);
        }
    }

    /**
     * Maps bot public key in PKCS#8 format of Strign type to {@link java.security.interfaces.RSAPublicKey} type
     * @return the rsa public key
     * @throws InvalidBotCredentialsException  If the public key string has the wrong format 
     *                                         or the algorithm is set incorrectly
     */
    public RSAPublicKey mapToRsaPublicKey() throws InvalidBotCredentialsException {
        try {
            Pattern pattern = Pattern.compile(RSA_PUBLIC_KEY_REGEX);
            String publicKeyStringWithoutHeaderAndFooter = pattern
                .matcher(messengerBot.getPublicKey())
                .results()
                .findFirst()
                .map(MatchResult::group)
                .orElseThrow(InvalidBotCredentialsException::new);

            String publicKeyStringWithoutLineSeparators = publicKeyStringWithoutHeaderAndFooter
                .replaceAll(System.lineSeparator(), "")
                .replaceAll(SPACE_REGEX, "");

            byte[] decodedPrivateKey = Base64.decodeBase64(publicKeyStringWithoutLineSeparators);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedPrivateKey);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new InvalidBotCredentialsException(e);
        }
    }
}
