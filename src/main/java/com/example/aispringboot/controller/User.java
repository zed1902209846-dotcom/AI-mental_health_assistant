package com.example.aispringboot.controller;

import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.common.Result;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class User {
    @PostMapping("/login")
    public Result<String> login(@Valid @RequestBody UserLoginCommandDTO CommandDTO) {
        System.out.println(CommandDTO.getUsername());
        System.out.println(CommandDTO.getPassword());
        return null;
       }
}
