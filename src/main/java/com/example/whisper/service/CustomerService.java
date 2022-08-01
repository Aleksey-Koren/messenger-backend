package com.example.whisper.service;

import com.example.whisper.entity.Customer;
import com.example.whisper.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer getById(UUID id) {
        return customerRepository.findById(id).orElseThrow(() -> {
            log.warn("No customer with id = {} in database", id);
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        });
    }
}
