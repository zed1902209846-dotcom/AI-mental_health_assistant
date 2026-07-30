package com.example.aispringboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Null;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public Result<UserLoginResponseDTO> login(UserLoginCommandDTO CommandDTO) {
        //构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, CommandDTO.getUsername())
                .or()
                .eq(User::getEmail, CommandDTO.getUsername());
        //调用 MP的 API查询
        User user = userMapper.selectOne(queryWrapper);
        System.out.print(user);
        return null;

    }
}
