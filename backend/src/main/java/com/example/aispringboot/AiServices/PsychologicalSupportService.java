package com.example.aispringboot.AiServices;

import com.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import com.example.aispringboot.DTO.response.ConsultationMessageResponseDTO;
import com.example.aispringboot.entity.ConsultationSession;
import com.example.aispringboot.service.ConsultationMessageService;
import com.example.aispringboot.service.ConsultationSessionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.List;


@Service
public class PsychologicalSupportService {
    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private ConsultationSessionService consultationSessionService;

    @Autowired
    private ConsultationMessageService consultationMessageService;

    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        // 创建数据库会话记录
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);

        //将初始用户消息保存到 message 表里面
        consultationMessageService.saveUserMessage(consultationSession.getId(), createDTO.getInitialMessage(), null);

        //创建会话信息
        String sessionId = "session_" + consultationSession.getId();
        return new StructOutPut.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L, //24小时
                1,
                "ACTIVE"
        );

    }

    public Flux<String> streamPsychologicalChat(String sessionId, String userMessage) {
        //创建响应流
        return Flux.create(sink -> {
            // sink.next("数据 1) 方法用于发送响应消息
            // sink.complete() 方法用于完成流
            // sink.error(exception) 方法用于发送错误消息
            Long dbSessionId = extractSessionId(sessionId);
            if(dbSessionId == null){
                sink.error(new RuntimeException("会话ID格式错误"));
                return;
            }
            //判断是否是初始消息
            boolean isInitialMessage = false;
            //检查是否是初始消息,避免重复保存
            Integer messageCount = consultationMessageService.getMessageCountBySessionId(dbSessionId);
            if (messageCount == 1){
                ConsultationMessageResponseDTO lastMessage = consultationMessageService.getLastMessageBySessionId(dbSessionId);
                    if (lastMessage != null && lastMessage.getSenderType() == 1 && userMessage.equals(lastMessage.getContent())){

                        isInitialMessage = true;
                    }
            }
            if (!isInitialMessage){
                //保存用户消息到 message 表里面
                consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
            }

            //进行流式对话
            //生成对话记忆管理
            String conversationId = "conversation_" + sessionId;
            //构建系统提示词
            List<Message> userMessages = new ArrayList<>();
            userMessages.add(new UserMessage(userMessage));
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
            ));

            //用于存储大模型返回的内容
            StringBuilder fullResponse = new StringBuilder();

            //使用 chatClient 进行流式对话,发送到模型
            chatClient.prompt(prompt)
                    .user(userMessage)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                    .stream()
                    .content()
                    .doOnNext(Fragment -> {
                        fullResponse.append(Fragment);
                        sink.next(Fragment);
                    })
                    //把大模型返回的内容保存到数据库里面
                    .doOnComplete(() -> {
                        String completeRes = fullResponse.toString();
                        consultationMessageService.saveAiMessage(dbSessionId, completeRes, "openai");
                        //添加 ai 回复到 chatmemory 中
                        List<Message> aiMessages = new ArrayList<>();
                        aiMessages.add(new AssistantMessage(completeRes));
                        chatMemory.add(conversationId, aiMessages);

                        sink.complete();
                    })
                    .doOnError(exception -> {
                        sink.error(exception);
                    })
                    .subscribe();//订阅并启动流
        });
    }

    //获取参数中的sessionId
    private Long extractSessionId(String sessionId) {
        if(sessionId != null && sessionId.startsWith("session_")){
            return Long.parseLong(sessionId.substring("session_".length()));
        }
        return null;
    }
}
