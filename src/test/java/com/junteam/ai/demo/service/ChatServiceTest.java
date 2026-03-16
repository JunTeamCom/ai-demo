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
    public void evaluateRelevancy() {
        String userText = "美国的首都是哪里？";
        ChatQuestion chatQuestion = new ChatQuestion(userText);
        System.out.println("=== Chat Debug Start ===");
        ChatAnswer chatAnswer = chatService.ask(chatQuestion);
        System.out.println("=== Chat Debug Info ===");
        System.out.println("Question: " + userText);
        System.out.println("Answer: " + chatAnswer.answer());
        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, chatAnswer.answer());
        System.out.println("=== Evaluator Debug Start ===");
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);
        // 添加调试打印
        System.out.println("=== Evaluator Debug Info ===");
        System.out.println("Score: " + evaluationResponse.getScore()); // 获取相关度
        System.out.println("Feedback: " + evaluationResponse.getFeedback()); // 获取相关度说明
        System.out.println("Raw Response: " + evaluationResponse.toString());
        System.out.println("============================");
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
