package com.example.aispringboot.service;

import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.Result;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public Result<UserLoginResponseDTO> login(UserLoginCommandDTO CommandDTO) {

    }
}
