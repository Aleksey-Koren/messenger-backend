package com.example.whisper.service;

import com.example.whisper.app_properties.MessageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final MessageProperties messageProperties;

    /*
        Properties to save:
            1. MessageProperties.lifespan
     */
    public void savePropsToUtilities() {
        messageProperties.saveLifespanToDb(messageProperties.getLifespan());
    }

}
