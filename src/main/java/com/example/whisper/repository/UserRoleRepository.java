package com.example.whisper.repository;

import com.example.whisper.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    List<UserRole> findAllByChatId(UUID chatId);

    Optional<UserRole> findByUserId(UUID userId);

    Optional<UserRole> findByUserIdAndChatId(UUID userId, UUID chatId);

    Optional<UserRole> findByUserIdAndChatIdAndUserType(UUID userId, UUID chatId, UserRole.UserType userType);

    @Transactional
    void deleteAllByUserIdAndChatId(UUID userId, UUID chatId);

    void deleteAllByChatId(UUID chatId);
}
