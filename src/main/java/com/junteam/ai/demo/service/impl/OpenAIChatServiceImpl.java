package com.junteam.ai.demo.service.impl;

import java.util.Objects;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.junteam.ai.demo.model.ChatAnswer;
import com.junteam.ai.demo.model.ChatQuestion;
import com.junteam.ai.demo.service.ChatService;

@Service
public class OpenAIChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public OpenAIChatServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public ChatAnswer ask(ChatQuestion question) {
        var answerText = chatClient.prompt()
                .user(Objects.requireNonNull(question.question()))
                .call()
                .content();
        return new ChatAnswer(answerText);
    }
}
