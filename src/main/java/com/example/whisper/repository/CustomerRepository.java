package com.example.whisper.repository;

import com.example.whisper.entity.Chat;
import com.example.whisper.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    @Query("select distinct chat.members from Chat chat where :customer member of chat.members")
    List<Customer> findDistinctWhereMembersOfChatsOfCustomer(@Param("customer") Customer customer);

    @Query("select chat.members from Chat chat where chat=:chat")
    List<Customer> findAllByChat(@Param("chat") Chat chat);
}
