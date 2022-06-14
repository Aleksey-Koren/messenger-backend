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
    //@Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;
   /// @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID sender;
   // @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID receiver;
    //@Type(type = "org.hibernate.type.UUIDCharType")
    private UUID chat;

    @Enumerated(EnumType.ORDINAL)
    private MessageType type;
    private String data;
    private String nonce;
    private Instant created;
}
