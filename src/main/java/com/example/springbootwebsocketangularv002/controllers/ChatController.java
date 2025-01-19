package com.example.springbootwebsocketangularv002.controllers;

import com.example.springbootwebsocketangularv002.entities.ChatMessage;
import com.example.springbootwebsocketangularv002.services.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChatController  {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    public static final Integer MAX_SIZE = 200;

    public ChatController(SimpMessagingTemplate messagingTemplate, ChatMessageService chatMessageService) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageService = chatMessageService;
    }

    // Handle incoming chat messages
    @MessageMapping("/sendMessage")
    public void handleMessage(ChatMessage message) {
        if (message.getContent().length() > MAX_SIZE) {
            return;
        }

        chatMessageService.saveMessage(message);
        chatMessageService.flush();

        messagingTemplate.convertAndSend("/topic/public", message);
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        chatMessageService.saveMessage(message);
        chatMessageService.flush();

        headerAccessor.getSessionAttributes().put("username", message.getUsername());
        return message;
    }

    @MessageMapping("/userTyping")
    public void handleTyping(ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/userTyping", message);
    }

    @SubscribeMapping("/history")
    public List<ChatMessage> getChatHistory() {
        return chatMessageService.getLast50Messages();
    }
}