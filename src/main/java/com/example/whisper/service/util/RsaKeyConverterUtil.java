package com.example.whisper.service.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Class that converts rsa keys to PEM format and PEM format to rsa keys.
 *
 * @author Maksim Semianko
 */
public class RsaKeyConverterUtil {

    //@TODO INFO I think more standard way how to handle that operations
    static private final String BEGIN_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----";
    static private final String END_PUBLIC_KEY = "-----END PUBLIC KEY-----";
    static private final String BEGIN_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----";
    static private final String END_PRIVATE_KEY = "-----END RSA PRIVATE KEY-----";
    static private final String LINE_SEPARATOR = System.lineSeparator();

    /**
     * Method that coverts public key to pem format.
     *
     * @param publicKey - key for convert
     * @return pem format of public key
     */
    public static String publicKeyToPem(PublicKey publicKey) {
        String key = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        return BEGIN_PUBLIC_KEY + LINE_SEPARATOR + keyToPem(key) + END_PUBLIC_KEY;
    }

    /**
     * Method that coverts private key to pem format.
     *
     * @param privateKey - key for convert
     * @return pem format of private key
     */
    public static String privateKeyToPem(PrivateKey privateKey) {
        String key = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        return BEGIN_PRIVATE_KEY + LINE_SEPARATOR + keyToPem(key) + END_PRIVATE_KEY;
    }

    /**
     * Method that converts pem format to public key.
     *
     * @param publicPem - pem for convert
     * @return the public key
     */
    public static PublicKey pemToPublicKey(String publicPem) {
        String clearPublicPem = publicPem
                .replace(BEGIN_PUBLIC_KEY, "")
                .replaceAll(LINE_SEPARATOR, "")
                .replace(END_PUBLIC_KEY, "");

        X509EncodedKeySpec encodedKeySpec = getKeySpecFromPublicPem(clearPublicPem);

        try {
            return KeyFactory.getInstance("RSA").generatePublic(encodedKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method that converts pem format to private key.
     *
     * @param privatePem - pem for convert
     * @return the private key
     */
    public static PrivateKey pemToPrivateKey(String privatePem) {
        String clearPrivatePem = privatePem
                .replace(BEGIN_PRIVATE_KEY, "")
                .replaceAll(LINE_SEPARATOR, "")
                .replace(END_PRIVATE_KEY, "");

        PKCS8EncodedKeySpec encodedKeySpec = getKeySpecFromPrivatePem(clearPrivatePem);

        try {
            return KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Method that returns EncodedKeySpec for public key.
     *
     * @param pem - pem format
     * @return EncodedKeySpec
     */
    private static X509EncodedKeySpec getKeySpecFromPublicPem(String pem) {
        byte[] encoded = Base64.getDecoder().decode(pem);
        return new X509EncodedKeySpec(encoded);
    }

    /**
     * Method that returns EncodedKeySpec for private key.
     *
     * @param pem - pem format
     * @return PKCS8EncodedKeySpec
     */
    private static PKCS8EncodedKeySpec getKeySpecFromPrivatePem(String pem) {
        byte[] encoded = Base64.getDecoder().decode(pem);
        return new PKCS8EncodedKeySpec(encoded);
    }

    /**
     * Method that transforms the row of key to pem format.
     *
     * @param key - the row
     * @return the pem format
     */
    private static String keyToPem(String key) {
        StringBuilder pemFormat = new StringBuilder();
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < key.length(); i++) {
            row.append(key.charAt(i));
            if (row.length() == 64 || i == key.length() - 1) {
                pemFormat.append(row).append(LINE_SEPARATOR);
                row = new StringBuilder();
            }
        }

        return String.valueOf(pemFormat);
    }
}
