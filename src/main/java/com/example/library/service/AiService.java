package com.example.library.service;

import com.example.library.dto.AiChatResponse;

public interface AiService {
    AiChatResponse ask(String question);
}
