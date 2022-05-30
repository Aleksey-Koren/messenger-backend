package com.example.whisper.repository;

import com.example.whisper.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID>, JpaSpecificationExecutor<Message> {

    //todo ((and MessageType = "hello") + message.data)
    @Query("select distinct message.chat from Message message where message.receiver = :receiver")
    List<UUID> findChats(@Param("receiver") UUID receiver);

    @Query("select distinct message.receiver from Message message where message.chat = :chat")
    List<UUID> findParticipants(@Param("chat") UUID chat);
}
