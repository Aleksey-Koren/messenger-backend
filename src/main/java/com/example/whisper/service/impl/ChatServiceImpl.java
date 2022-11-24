package com.example.whisper.service.impl;

import com.example.whisper.entity.Administrator;
import com.example.whisper.entity.Bot;
import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.AdministratorRepository;
import com.example.whisper.repository.BotRepository;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final CustomerRepository customerRepository;
    private final BotRepository botRepository;
    private final AdministratorRepository administratorRepository;
    private final MessageRepository messageRepository;

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
    @Transactional
    public void removeCustomerFromChat(UUID customerId, UUID chatId) {
        Customer customer = findCustomerById(customerId);
        Chat chat = findById(chatId);
        chat.getMembers().remove(customer);
        messageRepository.deleteAllByReceiverAndChatAndType(customerId, chatId, Message.MessageType.iam);
        messageRepository.deleteAllBySenderAndChatAndType(customerId, chatId, Message.MessageType.iam);
        messageRepository.deleteAllByReceiverAndChatAndType(customerId, chatId, Message.MessageType.hello);
        administratorRepository.deleteByUserIdAndChatId(customerId, chatId);

        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void leaveChat(UUID chatId, UUID customerId, Boolean withDeleteMessages) {
        Chat chat = findById(chatId);
        Customer customer = findCustomerById(customerId);

        if (withDeleteMessages) {
            messageRepository.deleteAllByReceiverAndSenderAndChat(customerId, customerId, chatId);
        } else {
            messageRepository.deleteAllByReceiverAndChatAndType(customerId, chatId, Message.MessageType.iam);
            messageRepository.deleteAllBySenderAndChatAndType(customerId, chatId, Message.MessageType.iam);
            messageRepository.deleteAllByReceiverAndChatAndType(customerId, chatId, Message.MessageType.hello);
        }

        chat.getMembers().remove(customer);
        chatRepository.save(chat);

        if (administratorRepository.findByUserIdAndChatId(customerId, chatId).isPresent()) {
            administratorRepository.deleteByUserIdAndChatId(customerId, chatId);
            assignRolesToOtherUsersOfChat(chat);
        }
    }

    private Customer findCustomerById(UUID uuid) {
        return customerRepository
                .findById(uuid)
                .orElseThrow(() -> new ResourseNotFoundException("Customer not found by id!"));
    }

    public List<UUID> findChatBots(UUID chatId) {
        return chatRepository.findById(chatId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid chat id"))
            .getMembers()
            .stream()
            .filter(Bot.class::isInstance)
            .map(Bot.class::cast)
            .map(Bot::getId)
            .toList();
            
    }

    private void assignRolesToOtherUsersOfChat(Chat chat) {
        List<Administrator> administratorList = administratorRepository.findAllByChatId(chat.getId());

        List<Administrator> listWithUserTypeAdmin = administratorList
                .stream()
                .filter(item -> item.getUserType().equals(Administrator.UserType.ADMINISTRATOR))
                .toList();

        if (listWithUserTypeAdmin.size() == 0) {
            if (administratorList.size() != 0) {
                administratorList.forEach(item -> item.setUserType(Administrator.UserType.ADMINISTRATOR));
            } else {
                administratorRepository.deleteAllByChatId(chat.getId());
                Set<Customer> members = chat.getMembers();
                Set<Administrator> administrators = new HashSet<>();
                for (Customer member : members) {
                    Administrator administrator = Administrator.builder()
                            .id(UUID.randomUUID())
                            .chatId(chat.getId())
                            .userId(member.getId())
                            .userType(Administrator.UserType.ADMINISTRATOR)
                            .build();

                    administrators.add(administrator);
                }

                administratorRepository.saveAll(administrators);
            }
        }
    }
}
