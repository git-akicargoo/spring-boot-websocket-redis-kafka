package com.example.boot_redis_kafka_mysql.domain.chat.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.boot_redis_kafka_mysql.domain.chat.dto.ChatMessageRequest;
import com.example.boot_redis_kafka_mysql.domain.chat.dto.ChatMessageResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatService {
    public ChatMessageResponse handleMessage(ChatMessageRequest request) {
        log.info("Processing chat message: {}", request);
        return new ChatMessageResponse(
            request.username(),
            request.content(),
            LocalDateTime.now()
        );
    }
} 