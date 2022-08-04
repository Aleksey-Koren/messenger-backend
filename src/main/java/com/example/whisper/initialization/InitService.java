package com.example.whisper.initialization;

import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.UtilRepository;
import com.iwebpp.crypto.TweetNaclFast;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UtilRepository utilRepository;
    private final CustomerRepository customerRepository;


    @Transactional
    public void recreateServerUser() {
        utilRepository.findById(Utility.Key.SERVER_USER_ID.name())
                .ifPresent(s -> customerRepository.deleteById(UUID.fromString(s.getUtilValue())));

        UUID id = UUID.randomUUID();
        TweetNaclFast.Box.KeyPair keyPair = TweetNaclFast.Box.keyPair();
        Customer serverCustomer = new Customer(id, Base64.getEncoder().encodeToString(keyPair.getPublicKey()));
        customerRepository.save(serverCustomer);

        utilRepository.save(new Utility(Utility.Key.SERVER_USER_ID.name(), id.toString()));
        utilRepository.save(new Utility(Utility.Key.SERVER_USER_SECRET_KEY.name(),
                Base64.getEncoder().encodeToString(keyPair.getSecretKey())));
    }
}