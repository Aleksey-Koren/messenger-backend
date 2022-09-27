package com.example.whisper.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Administrator {

    public enum UserType {
        SUPER_ADMINISTRATOR,
        ADMINISTRATOR,
        MODERATOR,
    }

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID userId;

    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID chatId;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private UserType userType;

}
