package com.example.whisper.service.impl;

import com.example.whisper.entity.Administrator;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;
    private final AdministratorRepository administratorRepository;

    @Override
    public Chat findById(UUID id) {
        return chatRepository
                .findById(id)
                .orElseThrow(() -> new ResourseNotFoundException("Chat not found by id!"));
    }

    @Override
    public Chat create(Chat chat) {
        Customer creator = findCustomerById(chat.getCreatorId());
        chat.setMembers(Set.of(creator));

        Chat savedChat = chatRepository.save(chat);

        Administrator administrator = new Administrator();
        administrator.setId(UUID.randomUUID());
        administrator.setUserId(chat.getCreatorId());
        administrator.setChatId(chat.getId());
        administrator.setUserType(Administrator.UserType.ADMINISTRATOR);

        administratorRepository.save(administrator);

        return savedChat;
    }

    @Override
    public Chat addCustomerToChat(UUID customerId, UUID chatId) {
        Customer customer = findCustomerById(customerId);
        Chat chat = findById(chatId);
        chat.getMembers().add(customer);

        return chatRepository.save(chat);
    }

    @Override
    public Page<Chat> findAllWhereCustomerIdIsMember(UUID id, Pageable pageable) {
        Customer customer = findCustomerById(id);
        return chatRepository.findAllWhereCustomerIsMember(customer, pageable);
    }

    private Customer findCustomerById(UUID uuid) {
        return customerRepository
                .findById(uuid)
                .orElseThrow(() -> new ResourseNotFoundException("Customer not found by id!"));
    }
}
