package com.example.whisper.initialization;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.UtilRepository;
import com.example.whisper.service.util.RsaKeyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UtilRepository utilRepository;
    private final CustomerRepository customerRepository;

    @Value("${app.init-props.key-rsa-size}")
    private int keySize;

    @Transactional
    public void recreateServerUser() throws NoSuchAlgorithmException {
        utilRepository.findById(Utility.Key.SERVER_USER_ID.name())
                .ifPresent(s -> customerRepository.deleteById(UUID.fromString(s.getUtilValue())));

        UUID id = UUID.randomUUID();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        String publicKeyPem = RsaKeyUtil.publicKeyToPem(keyPair.getPublic());
        String privateKeyPem = RsaKeyUtil.privateKeyToPem(keyPair.getPrivate());

        Customer serverCustomer = new Customer(id, publicKeyPem);
        customerRepository.save(serverCustomer);

        utilRepository.save(new Utility(Utility.Key.SERVER_USER_ID.name(), id.toString()));
        utilRepository.save(new Utility(Utility.Key.SERVER_USER_SECRET_KEY.name(), privateKeyPem));
    }

}