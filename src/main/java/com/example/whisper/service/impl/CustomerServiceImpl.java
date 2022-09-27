package com.example.whisper.service.impl;

import com.example.whisper.dto.MemberResponseDto;
import com.example.whisper.entity.Administrator;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.CustomerService;
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
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final AdministratorRepository administratorRepository;

    @Override
    public Customer findById(UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> {
            log.warn("No customer with id = {} in database", id);
            throw new ResourseNotFoundException("Customer not found by id!");
        });
    }

    @Override
    public List<Customer> findAllByIds(List<UUID> ids) {
        return customerRepository.findAllById(ids);
    }

    @Override
    public List<MemberResponseDto> findAllByChatId(UUID chatId) {
        Chat chat = chatRepository
                .findById(chatId)
                .orElseThrow(() -> new ResourseNotFoundException("Chat not found by id!"));

        List<Customer> customerList = customerRepository.findAllByChat(chat);
        List<Administrator> administratorList = administratorRepository.findAllByChatId(chatId);

        List<MemberResponseDto> memberResponseDtoList = new ArrayList<>();

        for (int i = 0; i < customerList.size(); i++) {
            int finalI = i;
            Optional<Administrator> optionalAdministrator = administratorList
                    .stream()
                    .filter(administrator -> administrator.getUserId().equals(customerList.get(finalI).getId()))
                    .findFirst();

            String role = optionalAdministrator.isPresent() ? optionalAdministrator.get().getUserType().toString() : "NONE";
            MemberResponseDto memberResponseDto = new MemberResponseDto();
            memberResponseDto.setId(customerList.get(finalI).getId());
            memberResponseDto.setPk(customerList.get(finalI).getPk());
            memberResponseDto.setRole(role);

            memberResponseDtoList.add(memberResponseDto);
        }

        return memberResponseDtoList;
    }

    @Override
    public List<Customer> getDistinctCustomersWhichMembersOfChatsByCustomerId(UUID customerId) {
        Customer customer = findById(customerId);
        return customerRepository.findDistinctWhereMembersOfChatsOfCustomer(customer);
    }

    @Override
    public Customer register(Customer customer) {
        customer.setId(UUID.randomUUID());
        return customerRepository.save(customer);
    }

    @Override
    public Void delete(Customer customer) {
        Customer customerToDelete = findById(customer.getId());

        try {
            byte[] bytes = Base64.getDecoder().decode(customer.getPk());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateFromPublic =
                    new X509EncodedKeySpec(customerToDelete.getPk().getBytes(StandardCharsets.UTF_8));
            PrivateKey pseudoPrivateKey = keyFactory.generatePrivate(privateFromPublic);

            Cipher decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, pseudoPrivateKey);

            byte[] decryptedMessageBytes = decryptCipher.doFinal(bytes);
            UUID decryptedMessage = UUID.fromString(new String(decryptedMessageBytes, StandardCharsets.UTF_8));
            if (decryptedMessage.equals(customerToDelete.getId())) {
                messageRepository.deleteMyMessages(customer.getId());
                customerRepository.delete(customerToDelete);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } catch (NoSuchAlgorithmException e) {
            log.error("Crypto error", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            log.error("invalid data in delete customer");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return null;
    }

}
