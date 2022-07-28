package com.example.whisper.app_properties;

import com.example.whisper.entity.Utility;
import com.example.whisper.repository.UtilRepository;
import com.example.whisper.service.UtilService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("app.message")
@RequiredArgsConstructor
public class MessageProperties {

    private final UtilRepository utilRepository;

    private Long lifespan;

    public void setLifespanToFieldAndDb(long lifespan) {
        utilRepository.save(new Utility(Utility.Key.MESSAGE_LIFESPAN.name(), String.valueOf(lifespan)));
        this.lifespan = lifespan;
    }

    public void setLifespan(Long lifespan) {
        this.lifespan = lifespan;
    }

    public Long getLifespan() {
        return this.lifespan;
    }
}