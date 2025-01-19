package com.example.springbootwebsocketangularv002.services;


import com.example.springbootwebsocketangularv002.entities.ChatMessage;
import com.example.springbootwebsocketangularv002.repositories.ChatMessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public void saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    public List<ChatMessage> getLast50Messages() {
        return chatMessageRepository.findLast50Messages(PageRequest.of(0, 50));
    }

    public void flush() {
        chatMessageRepository.flush();
    }
}
