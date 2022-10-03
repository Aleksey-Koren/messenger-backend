package com.example.whisper.controller;

import com.example.whisper.service.AdministratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("administrators")
@RequiredArgsConstructor
public class AdministratorController {

    private final AdministratorService administratorService;

}
