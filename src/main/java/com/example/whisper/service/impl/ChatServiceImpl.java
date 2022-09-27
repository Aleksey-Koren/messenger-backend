package com.example.whisper.service.impl;

import com.example.whisper.dto.CreateChatRequestDto;
import com.example.whisper.entity.Administrator;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.ServerMessageType;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;
    private final ServerMessageServiceImpl serverMessagesService;
    private final AdministratorRepository administratorRepository;

    @Override
    public Chat findById(UUID id) {
        return chatRepository
                .findById(id)
                .orElseThrow(() -> new ResourseNotFoundException("Chat not found by id!"));
    }

    @Override
    public Chat create(CreateChatRequestDto createChatRequestDto) {
        Customer creator = findCustomerById(createChatRequestDto.getCreatorId());
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID());
        chat.setTitle(createChatRequestDto.getTitle());
        chat.setMembers(Set.of(creator));

        Chat savedChat = chatRepository.save(chat);

        Administrator administrator = new Administrator();
        administrator.setId(UUID.randomUUID());
        administrator.setUserId(createChatRequestDto.getCreatorId());
        administrator.setChatId(chat.getId());
        administrator.setUserType(Administrator.UserType.ADMINISTRATOR);
        administratorRepository.save(administrator);

        return savedChat;
    }

    @Override
    public Chat updateTitleById(UUID uuid, String title) {
        Chat chat = findById(uuid);
        chat.setTitle(title);

        return chatRepository.save(chat);
    }

    @Override
    public Page<Chat> findAllWhereCustomerIdIsMember(UUID id, Pageable pageable) {
        Customer customer = findCustomerById(id);
        return chatRepository.findAllWhereCustomerIsMember(customer, pageable);
    }

    @Override
    public Chat addCustomer(UUID chatId, UUID customerId) {
        Chat chat = findById(chatId);
        Customer customer = findCustomerById(customerId);
        chat.getMembers().add(customer);

        return chatRepository.save(chat);
    }


    @Transactional
    @Override
    public Chat removeCustomer(UUID chatId, UUID customerId, String secretText, String nonce) {
        Chat chat = findById(chatId);
        Customer customer = findCustomerById(customerId);
        String decryptSecretText = serverMessagesService.decryptSecretText(customerId, secretText, nonce);
        ServerMessageType serverMessageType = serverMessagesService.getServerMessageType(decryptSecretText);

        switch (serverMessageType) {
            case LEAVE_CHAT -> {
                chat.setMembers(removeCustomerFromMembers(chat, customer));
                chat = chatRepository.save(chat);
            }
            case LEAVE_CHAT_WITH_DELETE_OWN_MESSAGES -> {
                chat.setMembers(removeCustomerFromMembers(chat, customer));
                chat = chatRepository.save(chat);
                messageRepository.deleteAllByReceiverAndSenderAndChat(customerId, customerId, chatId);
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid server message type!");
        }

        return chat;
    }

    private Set<Customer> removeCustomerFromMembers(Chat chat, Customer leaveCustomer) {
        return chat
                .getMembers()
                .stream()
                .filter(customer -> !customer.getId().equals(leaveCustomer.getId()))
                .collect(Collectors.toSet());
    }

    private Customer findCustomerById(UUID uuid) {
        return customerRepository
                .findById(uuid)
                .orElseThrow(() -> new ResourseNotFoundException("Customer not found by id!"));
    }
}
