package com.example.aispringboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aispringboot.DTO.command.UserLoginCommandDTO;
import com.example.aispringboot.DTO.response.UserLoginResponseDTO;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.exception.BusinessException;
import com.example.aispringboot.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Result<UserLoginResponseDTO> login(UserLoginCommandDTO commandDTO) {
        //构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, commandDTO.getUsername())
                .or()
                .eq(User::getEmail, commandDTO.getUsername());
        //调用 MP的 API查询
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);

        //判断用户是否存在
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        //判断密码是否正确
        String inputPassword = commandDTO.getPassword().trim();
        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        //检查用户状态
        if (!user.isActive()) {
            throw new BusinessException("用户已被禁用,请联系管理员");
        }
        return null;

    }
}
