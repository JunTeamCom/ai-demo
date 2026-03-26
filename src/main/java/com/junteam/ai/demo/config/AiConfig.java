package com.junteam.ai.demo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        // 构建RAG顾问
        var advisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                // 对聊天的问题进行加工处理
                .queryTransformers(
                        // 翻译问题
                        TranslationQueryTransformer.builder()
                                .chatClientBuilder(chatClientBuilder)
                                .targetLanguage("Chinese")
                                .build(),
                        // 重写（简化）问题
                        RewriteQueryTransformer.builder()
                                .chatClientBuilder(chatClientBuilder)
                                .build())
                // 扩展相关话题（扩展5个）
                .queryExpander(MultiQueryExpander.builder()
                        .chatClientBuilder(chatClientBuilder)
                        .numberOfQueries(5)
                        .includeOriginal(false)
                        .build())
                .build();
        // 设置顾问配置项
        return chatClientBuilder
                .defaultAdvisors(advisor)
                .build();
    }
}
