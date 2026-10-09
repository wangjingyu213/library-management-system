package com.example.library.controller;

import com.example.library.dto.AiChatRequest;
import com.example.library.dto.AiChatResponse;
import com.example.library.service.AiService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN', 'READER')")
    public AiChatResponse chat(@Valid @RequestBody AiChatRequest request) {
        return aiService.ask(request.getQuestion());
    }
}
