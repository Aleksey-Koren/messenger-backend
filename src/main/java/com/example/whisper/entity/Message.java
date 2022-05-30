package com.example.whisper.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Message {



    public enum MessageType {
        hello, iam, whisper
    }

    @Id
    private UUID id;
    private UUID sender;
    private UUID receiver;
    private UUID chat;

    private MessageType type;
    private String data;
    private String nonce;
    private Instant created;
}
