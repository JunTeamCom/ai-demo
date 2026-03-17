package com.junteam.ai.demo.service.impl;

import java.io.IOException;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.junteam.ai.demo.model.ChatAnswer;
import com.junteam.ai.demo.model.ChatQuestion;
import com.junteam.ai.demo.service.ChatService;

import jakarta.annotation.PostConstruct;

@Service
public class OpenAIChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public OpenAIChatServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Value("classpath:/promptTemplates/questionPromptTemplate.st")
    Resource questionPromptTemplate;

    @PostConstruct
    public void validateTemplate() throws IOException {
        if (questionPromptTemplate == null || !questionPromptTemplate.exists()) {
            throw new IllegalStateException("Prompt template not loaded properly");
        }
    }

    @SuppressWarnings("null")
    @Override
    public ChatAnswer ask(ChatQuestion chatQuestion) {
        var answer = chatClient.prompt()
                .user(userSpec -> userSpec
                    .text(questionPromptTemplate)
                    .param("title", chatQuestion.title())
                    .param("question", chatQuestion.question())
                )
                .call();
        var answerText = answer.content();
        return new ChatAnswer(chatQuestion.title(), answerText);
    }
}
