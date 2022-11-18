package com.example.whisper.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.whisper.entity.Bot;

public interface BotRepository extends JpaRepository<Bot, UUID> {
    
}
