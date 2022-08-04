package com.example.whisper.service;

import com.example.whisper.entity.Utility;
import com.example.whisper.repository.UtilRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilService {

    private final UtilRepository utilRepository;

    public List<Utility> findAll() {
        return utilRepository.findAll();
    }

    public UUID getServerUserId() {
        String id = utilRepository.findById(Utility.Key.SERVER_USER_ID.name()).orElseThrow(() -> {
            log.warn("No server user ID in utilities");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }).getUtilValue();
        return UUID.fromString(id);
    }
}