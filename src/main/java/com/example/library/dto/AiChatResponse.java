package com.example.library.dto;

public class AiChatResponse {

    private String answer;
    private String model;
    private int contextBookCount;

    public AiChatResponse() {
    }

    public AiChatResponse(String answer, String model, int contextBookCount) {
        this.answer = answer;
        this.model = model;
        this.contextBookCount = contextBookCount;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getContextBookCount() {
        return contextBookCount;
    }

    public void setContextBookCount(int contextBookCount) {
        this.contextBookCount = contextBookCount;
    }
}
