package com.example.whisper.repository;

import com.example.whisper.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message> {

    void deleteAllByReceiverAndSenderAndChat(UUID receiver, UUID sender, UUID chat);

    @Modifying
    @Transactional
    @Query("delete from Message message where message.receiver = :receiver")
    void deleteMyMessages(UUID receiver);

}