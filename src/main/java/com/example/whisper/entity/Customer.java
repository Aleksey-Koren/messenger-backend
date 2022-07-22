package com.example.whisper.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;


@Entity
@Getter
@Setter
@Table(name = "customer")
public class Customer {
    @Id
    private UUID id;
    @Column(name = "public_key")
    private String pk;
}
