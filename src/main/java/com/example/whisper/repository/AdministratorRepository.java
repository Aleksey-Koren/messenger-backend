package com.example.whisper.repository;

import com.example.whisper.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, UUID> {

    Optional<Administrator> findByUserId(UUID userId);

    Optional<Administrator> findByUserIdAndChatId(UUID userId, UUID chatId);

    List<Administrator> findAllByChatId(UUID chatId);

    void deleteByUserIdAndChatId(UUID userId, UUID chatId);
}
