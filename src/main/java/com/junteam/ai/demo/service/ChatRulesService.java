package com.junteam.ai.demo.service;

import java.util.stream.Collectors;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatRulesService {
    private VectorStore vectorStore;

    public ChatRulesService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public String getRulesFor(String countryTitle, String question) {
        var searchRequest = SearchRequest
                .builder()
                .query(question)
                .filterExpression(new FilterExpressionBuilder()
                        .eq("countryTitle", countryTitle)
                        .build())
                .build();
        log.info("Search Request:" + searchRequest);

        var similarDocuments = vectorStore.similaritySearch(searchRequest);
        if (similarDocuments.isEmpty()) {
            return countryTitle + "的信息不可用。";
        }
        return similarDocuments.stream()
                .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));
    }
}
