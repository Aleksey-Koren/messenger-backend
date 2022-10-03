package com.example.whisper.repository;

import com.example.whisper.entity.LastMessageCreated;
import com.example.whisper.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message> {

    @Query("select message from Message message where (message.receiver = :receiver and message.type = :messageType)")
    List<Message> findChats(@Param("receiver") UUID receiver, @Param("messageType") Message.MessageType messageType);

//    Optional<Message> findBySenderAndReceiverAndChatAndType()


    @Query("select message from Message message where (message.chat = :chat and message.type = :messageType)")
    List<Message> findAllByChatAndType(@Param("chat") UUID chat, @Param("messageType") Message.MessageType messageType);

    @Query(value = "SELECT chat as chat, MAX(created) as created FROM message WHERE chat IN (:chatsIds) GROUP BY chat", nativeQuery = true)
    List<LastMessageCreated> findLastMessageCreatedInChats(@Param("chatsIds") List<UUID> chatsIds);

    List<Message> findByChatAndSenderAndReceiverAndType(UUID chat, UUID sender, UUID receiver, Message.MessageType type);

    void deleteAllByReceiverAndSenderAndChat(UUID receiver, UUID sender, UUID chat);

    void deleteAllByReceiverAndSenderAndChatAndType(UUID receiver, UUID sender, UUID chat, Message.MessageType type);

    void deleteAllBySenderAndChatAndType(UUID sender, UUID chat, Message.MessageType type);

    @Modifying
    @Transactional
    @Query("delete from Message message where message.receiver = :receiver")
    void deleteMyMessages(UUID receiver);

    @Modifying
    @Transactional
    @Query("delete from Message message " +
            " where message.chat = :chat " +
            " and message.type = :type" +
            " and message.receiver in (:receivers)")
    void deleteHelloMessages(UUID chat, Message.MessageType type, List<UUID> receivers);

    @Transactional
    void deleteAllByChatAndSenderInAndReceiverAndType(UUID chat, List<UUID> sender, UUID receiver, Message.MessageType type);

    void deleteAllByReceiverAndChatAndType(UUID receiver, UUID chat, Message.MessageType type);

    void deleteAllBySenderAndType(UUID sender, Message.MessageType type);

    @Modifying
    @Transactional
    @Query("delete from Message message" +
            " where message.chat = :chat" +
            " and (message.receiver = :receiver or message.sender = :sender)")
    void deleteMyChatMessages(UUID receiver, UUID sender, UUID chat);

}