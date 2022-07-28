package com.example.whisper.service;

import com.example.whisper.app_properties.MessageProperties;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.UtilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final MessageProperties messageProperties;
    private final UtilRepository utilRepository;

    public Utility save(Utility entity) {
        return utilRepository.save(entity);
    }

    public void savePropsToUtilities() {
        messageProperties.setLifespanToFieldAndDb(messageProperties.getLifespan());
    }
}