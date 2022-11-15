package com.example.whisper.app_properties;
//@TODO INFO package name "app_properties" not conform java standard, should be "app.properties"
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("app.message")
@Getter
@Setter
public class MessageProperties {

    private Long lifespan;
    private String pathToAttachmentsFolder;
}