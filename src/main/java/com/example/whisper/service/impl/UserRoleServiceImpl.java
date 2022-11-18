package com.example.whisper.service.impl;

import com.example.whisper.dto.RequestRoleDto;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.UserRole;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.UserRoleRepository;
import com.example.whisper.service.UserRoleService;
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
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository administratorRepository;
    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;

    @Override
    public List<UserRole> findAllByChatId(UUID chatId) {
        return administratorRepository.findAllByChatId(chatId);
    }

    @Override
    @Transactional
    public UserRole createRoleByCustomerIdAndChatId(UUID customerId, UUID chatId, RequestRoleDto roleDto) {
        UserRole.UserType role = UserRole.UserType.valueOf(roleDto.getRole());

        Customer customer = getCustomerById(customerId);
        Chat chat = getChatById(chatId);
        checkMemberOfChat(chat, customer);
        checkCustomerIfRoleIsAlreadyExists(customerId, chatId, role);

        UserRole userRole = UserRole.builder()
                .id(UUID.randomUUID())
                .userId(customerId)
                .chatId(chatId)
                .userType(role)
                .build();

        return administratorRepository.save(userRole);
    }

    @Override
    public void deleteRoleByCustomerIdAndChatId(UUID customerId, UUID chatId) {
        administratorRepository.deleteAllByUserIdAndChatId(customerId, chatId);
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

    private void checkCustomerIfRoleIsAlreadyExists(UUID customerId, UUID chatId, UserRole.UserType role) {
        Optional<UserRole> optionalAdministrator =
                administratorRepository.findByUserIdAndChatIdAndUserType(customerId, chatId, role);
        if (optionalAdministrator.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer already has this role in chat!");
        } else {
            administratorRepository.deleteAllByUserIdAndChatId(customerId, chatId);
        }
    }

}
