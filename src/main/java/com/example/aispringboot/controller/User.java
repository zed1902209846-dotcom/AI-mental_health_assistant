package com.example.aispringboot.controller;

import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class User {

    @Resource
    private UserService userService;

    //用户登录接口
    @PostMapping("/login")
    public Result<UserLoginResponseDTO> login(@Valid @RequestBody UserLoginCommandDTO commandDTO) {

        //调用服务层方法
        UserLoginResponseDTO result = userService.login(commandDTO);
        System.out.println(result);
        return Result.ok(result);

       }
       //用户注册接口
    @PostMapping("/add")
    public Result<UserLoginResponseDTO.UserDetailResponseDTO> register(@Valid @RequestBody UserLoginCommandDTO commandDTO) {
        return null;
    }
}
