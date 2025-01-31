package com.example.boot_redis_kafka_mysql.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.boot_redis_kafka_mysql.dto.ChatMessageRequest;
import com.example.boot_redis_kafka_mysql.dto.ChatMessageResponse;
import com.example.boot_redis_kafka_mysql.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DemoWebSocketController {
    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/subscribe/chat")
    public ChatMessageResponse handleChatMessage(ChatMessageRequest request) {
        log.info("Received chat message: {}", request);
        ChatMessageResponse response = chatService.handleMessage(request);
        log.info("Sending chat response: {}", response);
        return response;
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ChatMessageResponse sendMessage(@RequestBody ChatMessageRequest request) {
        log.info("Received REST API chat message: {}", request);
        return chatService.handleMessage(request);
    }
}
