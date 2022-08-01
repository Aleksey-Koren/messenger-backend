package com.example.whisper.app_properties;

import com.example.whisper.entity.Utility;
import com.example.whisper.service.UtilService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("app.message")
@RequiredArgsConstructor
public class MessageProperties {

    private final UtilService utilService;

    @Getter
    @Setter
    private Long lifespan;

    public void saveLifespanToDb(long lifespan) {
        utilService.save(new Utility(Utility.Key.MESSAGE_LIFESPAN.name(), String.valueOf(lifespan)));
    }
}