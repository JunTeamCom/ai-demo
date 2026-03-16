package com.junteam.ai.demo.service;

import com.junteam.ai.demo.model.ChatAnswer;
import com.junteam.ai.demo.model.ChatQuestion;

public interface ChatService {
    ChatAnswer ask(ChatQuestion question);
}
