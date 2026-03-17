package com.junteam.ai.demo.service;

import java.io.IOException;
import java.nio.charset.Charset;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.junteam.ai.demo.model.ChatQuestion;
import com.junteam.ai.demo.service.impl.OpenAIChatServiceImpl;

/**
 *
 * @author gujun
 */
@EnableWireMock(@ConfigureWireMock(baseUrlProperties = "openai.base.url"))
@SpringBootTest(properties = "spring.ai.openai.base-url=${openai.base.url}")
public class ChatServiceMockTest {
    @Value("classpath:/test-openapi-response-usa.json")
    Resource responseResourceUSA;

    @Value("classpath:/test-openapi-response-uk.json")
    Resource responseResourceUK;

    @Autowired
    ChatClient.Builder chatClientBuilder;

    @BeforeEach
    public void setup() throws IOException{
        
    }

    public ChatServiceMockTest() {
    }

    /**
     * Test of ask method, of class OpenAIChatServiceImpl.
     * @throws IOException 
     */
    @SuppressWarnings("null")
    @Test
    public void testAsk() throws IOException {
        var cannedResponse = responseResourceUSA.getContentAsString(Charset.defaultCharset());
        var mapper = new ObjectMapper();
        var responseNode = mapper.readTree(cannedResponse);
        WireMock.stubFor(WireMock.post("/v1/chat/completions")
                .willReturn(ResponseDefinitionBuilder.okForJson(responseNode)));

        var instance = new OpenAIChatServiceImpl(chatClientBuilder);
        var chatAnswer = instance.ask(new ChatQuestion("美国的首都是哪里？"));
        Assertions.assertThat(chatAnswer).isNotNull();
        Assertions.assertThat(chatAnswer.answer()).isEqualTo("华盛顿");

        cannedResponse = responseResourceUK.getContentAsString(Charset.defaultCharset());
        responseNode = mapper.readTree(cannedResponse);
        WireMock.stubFor(WireMock.post("/v1/chat/completions")
                .willReturn(ResponseDefinitionBuilder.okForJson(responseNode)));
        chatAnswer = instance.ask(new ChatQuestion("英国的首都是哪里？"));
        Assertions.assertThat(chatAnswer).isNotNull();
        Assertions.assertThat(chatAnswer.answer()).isEqualTo("伦敦"); 
    }
}
