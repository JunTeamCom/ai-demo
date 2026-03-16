/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

package com.junteam.ai.demo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.junteam.ai.demo.model.ChatAnswer;
import com.junteam.ai.demo.model.ChatQuestion;
 
/**
 *
 * @author gujun
 */
@SpringBootTest
public class ChatServiceTest {
    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    private RelevancyEvaluator relevancyEvaluator;

    @BeforeEach
    public void setup() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
    }

    @Test
    public void evaluateRelevancy(){
        String userText = "Why is the sky blue?";
        ChatQuestion chatQuestion = new ChatQuestion(userText);
        ChatAnswer chatAnswer = chatService.ask(chatQuestion);
        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, chatAnswer.answer());
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
        Assertions.assertThat(evaluationResponse.isPass())
        .withFailMessage("""
                ========================================
                The answer "%s"
                is not considered relevant to the question 
                "%s".
                ========================================
                """, chatAnswer.answer(), userText)
                .isTrue();
    }
}
