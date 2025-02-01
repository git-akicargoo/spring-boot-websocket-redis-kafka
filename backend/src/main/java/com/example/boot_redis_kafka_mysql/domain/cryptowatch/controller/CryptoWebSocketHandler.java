package com.example.boot_redis_kafka_mysql.domain.cryptowatch.controller;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.config.CryptoFeatureConfig;
import com.example.boot_redis_kafka_mysql.domain.cryptowatch.dto.PriceDto;
import com.example.boot_redis_kafka_mysql.domain.cryptowatch.service.ExchangeWebSocketService.PriceListener;
import com.example.boot_redis_kafka_mysql.domain.cryptowatch.service.UpbitWebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CryptoWebSocketHandler extends TextWebSocketHandler implements PriceListener {
    
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final UpbitWebSocketService upbitService;
    private final ObjectMapper objectMapper;
    private final CryptoFeatureConfig featureConfig;

    @PostConstruct
    public void init() {
        upbitService.addPriceListener(this);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        sessions.add(session);
        log.info("Client connected: {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        sessions.remove(session);
        log.info("Client disconnected: {}", session.getId());
    }

    @Override
    public void onPriceUpdate(PriceDto priceDto) {
        try {
            // Redis 캐싱 (활성화된 경우)
            if (featureConfig.getRedis().isEnabled()) {
                // Redis 캐싱 로직은 나중에 구현
            }

            // Kafka 메시지 발행 (활성화된 경우)
            if (featureConfig.getKafka().isEnabled()) {
                // Kafka 발행 로직은 나중에 구현
            }

            // WebSocket 클라이언트들에게 브로드캐스트
            String message = objectMapper.writeValueAsString(priceDto);
            broadcastMessage(new TextMessage(message));
        } catch (Exception e) {
            log.error("Failed to process price update", e);
        }
    }

    private void broadcastMessage(TextMessage message) {
        sessions.forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (Exception e) {
                log.error("Failed to send message to client: {}", session.getId(), e);
            }
        });
    }
} 