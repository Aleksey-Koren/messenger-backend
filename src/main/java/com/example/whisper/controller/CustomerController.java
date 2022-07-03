package com.example.whisper.controller;

import com.example.whisper.entity.Customer;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;

    @PostMapping
    public ResponseEntity<Customer> register(@RequestBody Customer customer) {
        customer.setId(UUID.randomUUID());
        if(isEmpty(customer.getPk())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(customerRepository.save(customer));
    }

    @GetMapping
    public List<Customer> getCustomers(
            @RequestParam ("id") List<UUID> id
    ) {
        return customerRepository.findAllById(id);
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomer(
            @PathVariable ("id") UUID id
    ) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody Customer customer) {
        Optional<Customer> opt = customerRepository.findById(customer.getId());
        if(opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Customer db = opt.get();
        try {
            byte[] bytes = Base64.getDecoder().decode(customer.getPk());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateFromPublic = new X509EncodedKeySpec(db.getPk().getBytes(StandardCharsets.UTF_8));
            PrivateKey pseudoPrivateKey = keyFactory.generatePrivate(privateFromPublic);

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, pseudoPrivateKey);

            byte[] decryptedMessageBytes = decryptCipher.doFinal(bytes);
            UUID decryptedMessage = UUID.fromString(new String(decryptedMessageBytes, StandardCharsets.UTF_8));
            if(decryptedMessage.equals(db.getId())) {
                messageRepository.deleteMyMessages(customer.getId());
                customerRepository.delete(db);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("Crypto error", e);
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchPaddingException | IllegalBlockSizeException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            log.error("invalid data in delete customer");
            return ResponseEntity.badRequest().build();
        }
    }

    private boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

}
