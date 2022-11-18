package com.example.whisper.service.impl;

import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import com.example.whisper.entity.Message;
import com.example.whisper.entity.UserRole;
import com.example.whisper.exceptions.ResourseNotFoundException;
import com.example.whisper.repository.ChatRepository;
import com.example.whisper.repository.CustomerRepository;
import com.example.whisper.repository.MessageRepository;
import com.example.whisper.repository.UserRoleRepository;
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
    private final UserRoleRepository administratorRepository;
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

        UserRole userRole = new UserRole();
        userRole.setId(UUID.randomUUID());
        userRole.setUserId(chat.getCreatorId());
        userRole.setChatId(chat.getId());
        userRole.setUserType(UserRole.UserType.ADMINISTRATOR);

        administratorRepository.save(userRole);

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

        removeUserFromMembersAndDeleteRoleInChatIfExist(chat, customer);
        performDefaultDeletionOfMessages(chatId, customerId);

        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void leaveChat(UUID chatId, UUID customerId, Boolean withDeleteMessages) {
        Chat chat = findById(chatId);
        Customer customer = findCustomerById(customerId);

        removeUserFromMembersAndDeleteRoleInChatIfExist(chat, customer);

        if (withDeleteMessages) {
            messageRepository.deleteAllByReceiverAndSenderAndChat(customerId, customerId, chatId);
        } else {
            performDefaultDeletionOfMessages(chatId, customerId);
        }
    }

    private void removeUserFromMembersAndDeleteRoleInChatIfExist(Chat chat, Customer customer) {
        UUID chatId = chat.getId();
        UUID customerId = customer.getId();

        chat.getMembers().remove(customer);
        chatRepository.save(chat);

        if (administratorRepository.findByUserIdAndChatId(customerId, chatId).isPresent()) {
            administratorRepository.deleteAllByUserIdAndChatId(customerId, chatId);
            assignRolesToOtherUsersOfChat(chat);
        }
    }

    private void performDefaultDeletionOfMessages(UUID chatId, UUID customerId) {
        messageRepository.deleteAllByReceiverAndChatAndType(customerId, chatId, Message.MessageType.IAM);
        messageRepository.deleteAllBySenderAndChatAndType(customerId, chatId, Message.MessageType.IAM);
        messageRepository.deleteAllByReceiverAndChatAndType(customerId, chatId, Message.MessageType.HELLO);
    }


    private Customer findCustomerById(UUID uuid) {
        return customerRepository
                .findById(uuid)
                .orElseThrow(() -> new ResourseNotFoundException("Customer not found by id!"));
    }

    private void assignRolesToOtherUsersOfChat(Chat chat) {
        List<UserRole> userRoles = administratorRepository.findAllByChatId(chat.getId());

        List<UserRole> listWithUserTypeAdmin = userRoles
                .stream()
                .filter(item -> item.getUserType().equals(UserRole.UserType.ADMINISTRATOR))
                .toList();

        if (listWithUserTypeAdmin.size() == 0) {
            if (userRoles.size() != 0) {
                userRoles.forEach(item -> item.setUserType(UserRole.UserType.ADMINISTRATOR));
            } else {
                administratorRepository.deleteAllByChatId(chat.getId());
                Set<Customer> members = chat.getMembers();
                Set<UserRole> administrators = new HashSet<>();

                for (Customer member : members) {
                    UserRole administrator = UserRole.builder()
                            .id(UUID.randomUUID())
                            .chatId(chat.getId())
                            .userId(member.getId())
                            .userType(UserRole.UserType.ADMINISTRATOR)
                            .build();

                    administrators.add(administrator);
                }

                administratorRepository.saveAll(administrators);
            }
        }
    }
}
