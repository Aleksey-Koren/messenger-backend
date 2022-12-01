package com.example.whisper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
@ToString
@FieldNameConstants
public class Message {

    public enum MessageType {
        HELLO, //0
        IAM, //1
        WHISPER, //2
        WHO, //3
        SERVER,
        LEAVE_CHAT,
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

    @Column(length = 2048)
    private String data;

    private String attachments;

    @JsonIgnore
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<File> files = new HashSet<>();

    @Column(length = 361)
    private String nonce;

    private Instant created;
}
