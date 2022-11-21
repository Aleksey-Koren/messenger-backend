package com.example.whisper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
// @IntegrationComponentScan
public class WhisperApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhisperApplication.class, args);
    }

}