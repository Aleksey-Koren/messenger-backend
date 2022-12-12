package com.example.whisper.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class File {

    @Id
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String type;

    private String data;

    @ManyToOne
    @JoinColumn(name = "message_id", referencedColumnName = "id")
    @JsonIgnore
    private Message message;

    private String number;
}
