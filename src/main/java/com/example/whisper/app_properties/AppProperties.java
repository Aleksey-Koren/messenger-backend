package com.example.whisper.app_properties;

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

    private Boolean fetchPropsFromDbAtStartup;
    private Boolean isRecreateServerUserNeeded;
}