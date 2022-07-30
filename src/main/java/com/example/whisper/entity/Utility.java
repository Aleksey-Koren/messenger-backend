package com.example.whisper.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "utilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utility {

    @Id
    private String utilKey;
    private String utilValue;

    public enum Key {
        SERVER_USER_ID,
        SERVER_USER_SECRET_KEY,
        MESSAGE_LIFESPAN
    }
}