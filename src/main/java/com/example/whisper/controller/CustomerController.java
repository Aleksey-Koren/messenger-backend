package com.example.whisper.controller;

import com.example.whisper.entity.Customer;
import com.example.whisper.service.impl.CustomerServiceImpl;
import com.example.whisper.service.impl.UtilServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("customers")
@RequiredArgsConstructor
public class CustomerController {

    private final UtilServiceImpl utilService;
    private final CustomerServiceImpl customerService;

    @PostMapping
    public ResponseEntity<Customer> register(@RequestBody @Valid Customer customer) {
        return new ResponseEntity<>(customerService.register(customer), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Customer> findCustomersByIds(@RequestParam("id") List<UUID> ids) {
        return customerService.findAllByIds(ids);
    }

    @GetMapping("{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(customerService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestBody Customer customer) {
        return new ResponseEntity<>(customerService.delete(customer), HttpStatus.OK);
    }

    @GetMapping("server")
    public ResponseEntity<Customer> getServerCustomer() {
        return ResponseEntity.ok(customerService.findById(utilService.getServerUserId()));
    }

}