package com.junteam.ai.demo.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.junteam.ai.demo.model.ChatAnswer;
import com.junteam.ai.demo.model.ChatQuestion;
import com.junteam.ai.demo.service.ChatService;

import static org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor.FILTER_EXPRESSION;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@Service
public class OpenAIChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public OpenAIChatServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Value("classpath:/promptTemplates/questionPromptTemplate.st")
    Resource questionPromptTemplate;

    @Override
    public ChatAnswer ask(ChatQuestion chatQuestion, String conversationId) {
        var countryTitleMatch = String.format(
            "countryTitle == '%s'",
            chatQuestion.title());

        return chatClient
            .prompt()
            .system(systemSpec -> systemSpec
                .text(questionPromptTemplate)
                .param("countryTitle", chatQuestion.title()))
            .advisors(advisorSpec -> advisorSpec
                .param(FILTER_EXPRESSION, countryTitleMatch)
                .param(CONVERSATION_ID, conversationId)) // 引入会话ID
            .user(chatQuestion.question())
            .call()
            .entity(ChatAnswer.class);
    }
}
