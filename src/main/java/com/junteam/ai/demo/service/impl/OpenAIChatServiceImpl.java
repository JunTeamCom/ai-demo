package com.junteam.ai.demo.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.junteam.ai.demo.model.ChatAnswer;
import com.junteam.ai.demo.model.ChatQuestion;
import com.junteam.ai.demo.service.ChatRulesService;
import com.junteam.ai.demo.service.ChatService;

@Service
public class OpenAIChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatRulesService chatRulesService;

    public OpenAIChatServiceImpl(ChatClient.Builder chatClientBuilder, ChatRulesService chatRulesService) {
        this.chatClient = chatClientBuilder.build();
        this.chatRulesService = chatRulesService;
    }

    @Value("classpath:/promptTemplates/questionPromptTemplate.st")
    Resource questionPromptTemplate;

    @Override
    public ChatAnswer ask(ChatQuestion chatQuestion) {
        var chatRules = chatRulesService.getRulesFor(chatQuestion.title(), chatQuestion.question());
        var answer = chatClient
                .prompt()
                .system(systemSpec -> systemSpec
                        .text(questionPromptTemplate)
                        .param("countryTitle", chatQuestion.title())
                        .param("rules", chatRules))
                .user(chatQuestion.question())
                .call();
        var answerText = answer.content();
        return new ChatAnswer(chatQuestion.title(), answerText);
    }
}
