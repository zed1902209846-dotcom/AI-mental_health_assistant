package com.example.aispringboot.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.aispringboot.AiServices.PsychologicalSupportService;
import com.example.aispringboot.AiServices.StructOutPut;
import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.common.Result;
import com.example.aispringboot.util.JwtTokenUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/psychological-chat")
public class PsychologicalChat {
    @Autowired
    private PsychologicalSupportService psychologicalSupportService;
    @PostMapping("/session/start")
    public Result<StructOutPut.StreamChatSession> startSession(@Valid @RequestBody ConsultationSessionCreateDTO createDTO) {
        //获取当前用户 ID
        String token = JwtTokenUtil.getCurrentToken();
        DecodedJWT jwt = JwtTokenUtil.verifyToken(token);
        Long userId = jwt.getClaim("userId").asLong();
        psychologicalSupportService.startSession(userId,createDTO);
        return null;
    }
}
