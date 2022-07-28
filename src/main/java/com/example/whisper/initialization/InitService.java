package com.example.whisper.initialization;

import com.example.whisper.app_properties.MessageProperties;
import com.example.whisper.entity.Utility;
import com.example.whisper.repository.UtilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitService {

    private final UtilRepository utilRepository;
    private final MessageProperties messageProperties;

    public void fetchPropsFromDb() {
        utilRepository.findById(Utility.Key.MESSAGE_LIFESPAN.name())
                .ifPresent(s -> messageProperties.setLifespan(Long.parseLong(s.getUtilValue())));
    }

//    public void savePropsToDb() {
//        messageProperties.setLifespanToFieldAndDb(messageProperties.getLifespan());
//    }
}