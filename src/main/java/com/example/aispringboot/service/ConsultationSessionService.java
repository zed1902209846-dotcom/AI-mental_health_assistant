package com.example.aispringboot.service;

import cn.hutool.core.date.DateUtil;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.entity.ConsultationSession;
import com.example.aispringboot.entity.User;
import com.example.aispringboot.mapper.ConsultationSessionMapper;
import com.example.aispringboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsultationSessionService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO){
        //验证用户是否存在
        User user = userMapper.selectById(userId);
        if(user != null){
            //创建会话记录
            ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            //如果未提供标题，设置默认标题
            if(session.getSessionTitle() == null){
                session.setSessionTitle("云雀助手 - " + DateUtil.format(LocalDateTime.now(), "MM-dd HH:mm"));
            }
            //插入会话记录到数据库
            consultationSessionMapper.insert(session);
            return session;
        }

        return null;
    }
}
