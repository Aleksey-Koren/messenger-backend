package com.example.whisper.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "message")
@ToString
public class Message {

    public enum MessageType {
         iam, whisper, hello, who
    }

    @Id
    private UUID id;
    private UUID sender;
    private UUID receiver;
    private UUID chat;

    @Enumerated(EnumType.ORDINAL)
    private MessageType type;
    private String data;
    private String nonce;
    private Instant created;
}
