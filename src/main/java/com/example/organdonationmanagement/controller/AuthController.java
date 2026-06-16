package com.example.organdonationmanagement.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @GetMapping("/test")
    public String test() {
        return "Auth API OK";
    }
}
