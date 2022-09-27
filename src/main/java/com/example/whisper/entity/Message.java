package com.example.whisper.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
@ToString
public class Message {

    public enum MessageType {
        CHAT, // Chat message (example: edit chat title)
        WHISPER, // Just customer message
        INVITE_CHAT, // Add customer to chat
        LEAVE_CHAT, // Customer leave chat
    }

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @NotNull
    private UUID sender;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @NotNull
    private UUID receiver;

    @Type(type = "org.hibernate.type.UUIDCharType")
    @NotNull
    private UUID chat;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private MessageType type;

    private String data;

    private String attachments;

    private String nonce;

    private Instant created;
}
