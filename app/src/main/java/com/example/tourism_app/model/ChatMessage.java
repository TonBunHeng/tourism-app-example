package com.example.tourism_app.model;

public class ChatMessage {
    private String text;
    private boolean isUserMessage;
    private String timestamp;

    public ChatMessage(String text, boolean isUserMessage, String timestamp) {
        this.text = text;
        this.isUserMessage = isUserMessage;
        this.timestamp = timestamp;
    }

    public String getText() { return text; }
    public boolean isUserMessage() { return isUserMessage; }
    public String getTimestamp() { return timestamp; }
}