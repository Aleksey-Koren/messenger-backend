package com.example.whisper.initialization;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.UtilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UtilRepository utilRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public void recreateServerUser() throws NoSuchAlgorithmException {
        utilRepository.findById(Utility.Key.SERVER_USER_ID.name())
                .ifPresent(s -> customerRepository.deleteById(UUID.fromString(s.getUtilValue())));


        UUID id = UUID.randomUUID();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();


        String publicKeyPem = publicKeyToPem(keyPair.getPublic().getEncoded());
        String privateKeyPem = privateKeyToPem(keyPair.getPrivate().getEncoded());

//        TweetNaclFast.Box.KeyPair keyPair = TweetNaclFast.Box.keyPair();
        Customer serverCustomer = new Customer(id, publicKeyPem);
        customerRepository.save(serverCustomer);

        System.out.println(publicKeyPem);

        utilRepository.save(new Utility(Utility.Key.SERVER_USER_ID.name(), id.toString()));
        utilRepository.save(new Utility(Utility.Key.SERVER_USER_SECRET_KEY.name(), privateKeyPem));
    }

    private String publicKeyToPem(byte[] keyBytes) {
        String name = "PUBLIC KEY";
        String key = Base64.getEncoder().encodeToString(keyBytes);
        return "-----BEGIN " + name + "-----" + System.lineSeparator() + keyToPem(key) + "-----END " + name + "-----";
    }

    private String privateKeyToPem(byte[] keyBytes) {
        String name = "RSA PRIVATE KEY";
        String key = Base64.getEncoder().encodeToString(keyBytes);
        return "-----BEGIN " + name + "-----" + System.lineSeparator() + keyToPem(key) + "-----END " + name + "-----";
    }

    private StringBuilder keyToPem(String key) {
        StringBuilder pemFormat = new StringBuilder();
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < key.length(); i ++) {
            row.append(key.charAt(i));
            if (row.length() == 64 || i == key.length() - 1) {
                pemFormat.append(row).append(System.lineSeparator());
                row = new StringBuilder();
            }
        }

        return pemFormat;
    }

}