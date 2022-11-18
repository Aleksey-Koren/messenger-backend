package com.example.whisper.app.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("app.init-props")
@Getter
@Setter
public class AppProperties {

    private Boolean isRecreateServerUserNeeded;
}