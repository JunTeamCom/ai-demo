package com.junteam.ai.demo.config;

import javax.sql.DataSource;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.memory.repository.jdbc.MysqlChatMemoryRepositoryDialect;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    ChatMemoryRepository chatMemoryRepository(DataSource dataSource) {
        return JdbcChatMemoryRepository.builder()
            .dialect(new MysqlChatMemoryRepositoryDialect())
            .dataSource(dataSource)
            .build();
    }

    @Bean
    ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
            // 最大条数50，即：提问25条、回答25条
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(50)
            .build();
    }

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder,
        ChatMemory chatMemory,
        VectorStore vectorStore) {
        // 设置顾问配置项
        return chatClientBuilder
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory)
                    .build(),
                QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(SearchRequest.builder().build())
                    .build())
            .build();
    }
}
