package com.junteam.ai.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.junteam.ai.demo.model.ChatAnswer;
import com.junteam.ai.demo.model.ChatQuestion;
import com.junteam.ai.demo.service.ChatService;

@RestController
@RequestMapping("/web")
public class WebController {
    @Autowired
    private ChatService chatService;
    @PostMapping(path = "/ask", produces = "application/json")
    public ChatAnswer ask(@RequestBody ChatQuestion chatQuestion) {
        return chatService.ask(chatQuestion);
    }
}
