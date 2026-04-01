package com.junteam.ai.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder,
        VectorStore vectorStore) {
        // 设置顾问配置项
        return chatClientBuilder
            .defaultAdvisors(
                VectorStoreChatMemoryAdvisor
                    .builder(vectorStore)
                    .build(),
                QuestionAnswerAdvisor
                    .builder(vectorStore)
                    .searchRequest(SearchRequest.builder().build())
                    .build())
            .build();
    }
}
