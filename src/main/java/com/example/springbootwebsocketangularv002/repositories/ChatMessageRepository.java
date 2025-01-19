package com.example.springbootwebsocketangularv002.repositories;

import com.example.springbootwebsocketangularv002.entities.ChatMessage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m ORDER BY m.timestamp ASC")
    List<ChatMessage> findLast50Messages(PageRequest pageRequest);
}
