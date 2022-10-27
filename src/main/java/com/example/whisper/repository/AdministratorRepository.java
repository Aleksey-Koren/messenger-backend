package com.example.whisper.repository;

import com.example.whisper.entity.Administrator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, UUID> {

    List<Administrator> findAllByChatId(UUID chatId);

    Optional<Administrator> findByUserId(UUID userId);

    Optional<Administrator> findByUserIdAndChatId(UUID userId, UUID chatId);

    Optional<Administrator> findByUserIdAndChatIdAndUserType(UUID userId, UUID chatId, Administrator.UserType userType);

    @Transactional
    void deleteByUserIdAndChatId(UUID userId, UUID chatId);

    @Transactional
    void deleteAllByUserIdAndChatId(UUID userId, UUID chatId);

    void deleteAllByChatId(UUID chatId);
}
