package com.example.aispringboot.AiServices;

import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import org.springframework.stereotype.Service;

@Service
public class PsychologicalSupportService {
    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        // 创建数据库会话记录
        String sessionTitle = createDTO.getSessionTitle();
        return null;
    }
}
