package com.example.springbootwebsocketangularv002.controllers;

import com.example.springbootwebsocketangularv002.entities.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.lang.reflect.Type;

import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatControllerTest {

    @LocalServerPort
    private Integer port;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    void setup() {
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }


    @Test
    void verifyMessageIsSentAndSubscriptionToHistory() throws Exception {
        ChatMessage actualMessage = new ChatMessage("asd", "asd");

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/chat", port), new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);



        session.subscribe("/topic/public", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                ChatMessage message = (ChatMessage) payload;
                assertEquals(message.getContent(), actualMessage.getContent());
            }
        });

        session.send("/app/sendMessage", actualMessage);

        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);

        session.subscribe("/topic/history", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return List.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println(payload);
                String username = (String) ((LinkedHashMap) ((ArrayList) payload).get(0)).get("username");
                String content = (String) ((LinkedHashMap) ((ArrayList) payload).get(0)).get("content");


                assertEquals(actualMessage.getUsername(), username);
                assertEquals(actualMessage.getContent(), content);

                assertTrue((Integer) ((LinkedHashMap) ((ArrayList) payload).get(0)).get("id") > 0);
            }
        });

        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
        Thread.sleep(1000);
    }

}

