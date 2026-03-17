package com.junteam.ai.demo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private FactCheckingEvaluator factCheckingEvaluator;

    @BeforeEach
    public void setup() {
        this.relevancyEvaluator = new RelevancyEvaluator(chatClientBuilder);
        this.factCheckingEvaluator = FactCheckingEvaluator.builder(chatClientBuilder).build();
    }

    @Test
    public void evaluateRelevancy() {
        String userTitle = "美国";
        String userText = "美国的首都是哪里？";
        ChatQuestion chatQuestion = new ChatQuestion(userTitle, userText);
        System.out.println("=== Chat Debug Start ===");
        ChatAnswer chatAnswer = chatService.ask(chatQuestion);
        System.out.println("=== Chat Debug Info ===");
        System.out.println("Question: " + userText);
        System.out.println("Answer: " + chatAnswer.answer());

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, chatAnswer.answer());
        // 相关性评估：
        var response = relevancyEvaluator.evaluate(evaluationRequest);

        System.out.println("=== Evaluator Debug Info ===");
        System.out.println("Score: " + response.getScore()); // 获取相关度
        System.out.println("Feedback: " + response.getFeedback()); // 获取相关度说明
        System.out.println("Raw Response: " + response.toString());
        System.out.println("============================");

        Assertions.assertThat(response.isPass())
                .withFailMessage("""
                        ========================================
                        The answer "%s"
                        is not considered relevant to the question
                        "%s".
                        ========================================
                        """, chatAnswer.answer(), userText)
                .isTrue();
    }

    @SuppressWarnings({"null", "CallToPrintStackTrace"})
    private EvaluationResponse factCheckingEvaluateWithQwen(EvaluationRequest evaluationRequest) {
        var client = chatClientBuilder.build();

        // 构造显式的中文 Prompt，强制要求 JSON 输出
        String prompt = String.format("""
                你是一个事实核查助手。
                问题：%s
                回答：%s

                请判断上述回答是否符合客观事实。
                请仅返回一个 JSON 对象，不要包含任何其他文字。格式如下：
                {"pass": true/false, "score": 1.0或0.0, "feedback": "简短的理由"}
                """, evaluationRequest.getUserText(), evaluationRequest.getResponseContent());
        var mapper = new ObjectMapper();
        try {
            String content = client.prompt(prompt).call().content();
            System.out.println("Custom Evaluator Raw Response: " + content);

            // 简单解析 JSON (实际项目中建议用 Jackson ObjectMapper)
            var responseNode = mapper.readTree(content);
            return new EvaluationResponse(
                    responseNode.get("pass").asBoolean(),
                    (float) responseNode.get("score").asDouble(),
                    responseNode.get("feedback").asText(),
                    null
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new EvaluationResponse(false, 0.0f, e.getMessage(), null);
        }
    }

    @Test
    public void evaluateFactualAccuracy() {
        String userTitle = "美国";
        String userText = "美国首都是哪里？";
        ChatQuestion chatQuestion = new ChatQuestion(userTitle, userText);
        System.out.println("=== Chat Debug Start ===");
        ChatAnswer chatAnswer = chatService.ask(chatQuestion);
        System.out.println("=== Chat Debug Info ===");
        System.out.println("Question: " + userText);
        System.out.println("Answer: " + chatAnswer.answer());

        var answer0 = "华盛顿特区";
        EvaluationRequest evaluationRequest0 = new EvaluationRequest(userText, answer0);

        System.out.println("=== Evaluator Debug Start ===");
        // 实时正确性：
        var response0 = factCheckingEvaluator.evaluate(evaluationRequest0);

        // 添加调试打印
        System.out.println("=== Evaluator0 Debug Info ===");
        System.out.println("Score: " + response0.getScore()); // 获取相关度
        System.out.println("Feedback: " + response0.getFeedback()); // 获取相关度说明
        System.out.println("Raw Response: " + response0.toString());

        EvaluationRequest evaluationRequest = new EvaluationRequest(userText, chatAnswer.answer());
        // 事实准确性评估：
        var response = factCheckingEvaluator.evaluate(evaluationRequest);

        System.out.println("=== Evaluator Debug Info ===");
        System.out.println("Score: " + response.getScore()); // 获取相关度
        System.out.println("Feedback: " + response.getFeedback()); // 获取相关度说明
        System.out.println("Raw Response: " + response.toString());
        System.out.println("============================");

        var response1 = factCheckingEvaluateWithQwen(evaluationRequest);

        System.out.println("=== Evaluator1 Debug Info ===");
        System.out.println("Score: " + response1.getScore()); // 获取相关度
        System.out.println("Feedback: " + response1.getFeedback()); // 获取相关度说明
        System.out.println("Raw Response: " + response1.toString());
        System.out.println("============================");

        Assertions.assertThat(response1.isPass())
                .withFailMessage("""
                        ========================================
                        The answer "%s"
                        is not considered factually accurate to the question
                        "%s".
                        ========================================
                        """, chatAnswer.answer(), userText)
                .isTrue();

        // Assertions.assertThat(response0.isPass())
        //         .withFailMessage("""
        //                 ========================================
        //                 The answer "%s"
        //                 is not considered correct to the question
        //                 "%s".
        //                 ========================================
        //                 """, answer0, userText)
        //         .isTrue();

        // Assertions.assertThat(response.isPass())
        //         .withFailMessage("""
        //                 ========================================
        //                 The answer "%s"
        //                 is not considered correct to the question
        //                 "%s".
        //                 ========================================
        //                 """, chatAnswer.answer(), userText)
        //         .isTrue();
    }
}
