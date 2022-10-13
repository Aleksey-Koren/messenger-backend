package com.example.whisper.service.impl;

import com.example.whisper.entity.Administrator;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Chat;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdministratorServiceImpl implements AdministratorService {

    private final AdministratorRepository administratorRepository;
    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;

    @Override
    public Administrator createRole(UUID customerId, UUID chatId, Administrator.UserType role) {
        Customer customer = getCustomerById(customerId);
        Chat chat = getChatById(chatId);
        checkMemberOfChat(chat, customer);
        checkCustomerIfRoleIsAlreadyExists(customerId, chatId);
//        checkAdministratorOfChat(administratorId, chatId);

        Administrator administrator = new Administrator();
        administrator.setId(UUID.randomUUID());
        administrator.setUserId(customerId);
        administrator.setChatId(chatId);
        administrator.setUserType(role);

        return administratorRepository.save(administrator);
    }

    @Override
    public void deleteRole(UUID customerId, UUID chatId, UUID administratorId) {
        checkAdministratorOfChat(administratorId, chatId);

        administratorRepository.deleteByUserIdAndChatId(customerId, chatId);
    }

    private Customer getCustomerById(UUID uuid) {
        return customerRepository
                .findById(uuid)
                .orElseThrow(() -> new ResourseNotFoundException("Customer not found by id!"));
    }

    private Chat getChatById(UUID uuid) {
        return chatRepository
                .findById(uuid)
                .orElseThrow(() -> new ResourseNotFoundException("Chat not found by id!"));
    }

    private void checkMemberOfChat(Chat chat, Customer customer) {
        if (!chat.getMembers().contains(customer)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not member of chat!");
        }
    }

    private void checkCustomerIfRoleIsAlreadyExists(UUID customerId, UUID chatId) {
        Optional<Administrator> optionalAdministrator =
                administratorRepository.findByUserIdAndChatId(customerId, chatId);
        if (optionalAdministrator.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer already has a role in chat!");
        }
    }

    private void checkAdministratorOfChat(UUID administratorId, UUID chatId) {
        Optional<Administrator> optionalAdministrator = administratorRepository.findById(administratorId);
        if (optionalAdministrator.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Administrator was not found!");
        } else if (!optionalAdministrator.get().getChatId().equals(chatId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid administrator of chat!");
        }
    }

}
