package com.example.whisper.repository;

import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("select chat from Chat chat where :customer member of chat.members")
    Page<Chat> findAllWhereCustomerIsMember(@Param("customer") Customer customer, Pageable pageable);

}
