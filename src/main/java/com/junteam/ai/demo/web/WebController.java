package com.junteam.ai.demo.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/web")
public class WebController {
    @PostMapping(path = "/ask", produces = "application/json")
    public String ask(String question) {
        // TODO: OpenAI Chat API
        return "你问的问题是：" + question;
    }
}
