package com.example.aispringboot.AiServices;

public class StructOutPut {
    public record StreamChatSession(
            String sessionId,
            Long userHash,
            String initialMessage,
            Long startTime,
            Long expirationTime,
            Integer messageCount,
            String status
    ) {}
}
