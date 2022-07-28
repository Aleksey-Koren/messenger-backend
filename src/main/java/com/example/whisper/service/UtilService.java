package com.example.whisper.service;

import com.example.whisper.app_properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilService {

    private final MessageProperties messageProperties;

    public void savePropsToUtilities() {
        messageProperties.setLifespanToFieldAndDb(messageProperties.getLifespan());
    }
}