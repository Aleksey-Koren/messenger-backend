package com.example.whisper.service.impl;

import com.example.whisper.dto.RequestRoleDto;
import com.example.whisper.entity.Administrator;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    public List<Administrator> findAllByChatId(UUID chatId) {
        return administratorRepository.findAllByChatId(chatId);
    }

    @Override
    @Transactional
    public Administrator createRoleByCustomerIdAndChatId(RequestRoleDto roleDto) {
        UUID customerId = roleDto.getCustomerId();
        UUID chatId = roleDto.getChatId();
        Administrator.UserType role = Administrator.UserType.valueOf(roleDto.getRole());

        Customer customer = getCustomerById(customerId);
        Chat chat = getChatById(chatId);
        checkMemberOfChat(chat, customer);
        checkCustomerIfRoleIsAlreadyExists(customerId, chatId, role);

        Administrator administrator = new Administrator();
        administrator.setId(UUID.randomUUID());
        administrator.setUserId(customerId);
        administrator.setChatId(chatId);
        administrator.setUserType(role);

        return administratorRepository.save(administrator);
    }

    @Override
    public void deleteRoleByCustomerIdAndChatId(UUID customerId, UUID chatId) {
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer is not member of chat!");
        }
    }

    private void checkCustomerIfRoleIsAlreadyExists(UUID customerId, UUID chatId, Administrator.UserType role) {
        Optional<Administrator> optionalAdministrator =
                administratorRepository.findByUserIdAndChatIdAndUserType(customerId, chatId, role);
        if (optionalAdministrator.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer already has this role in chat!");
        } else {
            administratorRepository.deleteAllByUserIdAndChatId(customerId, chatId);
        }
    }

}
