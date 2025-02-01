package com.example.boot_redis_kafka_mysql.domain.cryptowatch.controller;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
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
    
    private final AtomicInteger connectionCount = new AtomicInteger(0);
    private final AtomicInteger requestCount = new AtomicInteger(0);
    
    @Value("${WS_MAX_CONNECTIONS_PER_SECOND:4}")
    private int maxConnectionsPerSecond;
    
    @Value("${WS_MAX_REQUESTS_PER_SECOND:4}")
    private int maxRequestsPerSecond;

    @PostConstruct
    public void init() {
        upbitService.addPriceListener(this);
        // 매 초마다 카운터 초기화
        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(() -> {
                connectionCount.set(0);
                requestCount.set(0);
            }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        if (connectionCount.incrementAndGet() > maxConnectionsPerSecond) {
            try {
                session.close(CloseStatus.POLICY_VIOLATION);
                return;
            } catch (IOException e) {
                log.error("Error closing connection", e);
            }
        }
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
        if (requestCount.incrementAndGet() > maxRequestsPerSecond) {
            log.debug("Request limit reached, skipping update");
            return;
        }
        
        try {
            if (featureConfig.getRedis().isEnabled()) {
                // Redis 캐싱 로직
            }

            if (featureConfig.getKafka().isEnabled()) {
                // Kafka 발행 로직
            }

            String message = objectMapper.writeValueAsString(priceDto);
            broadcastMessage(message);
        } catch (Exception e) {
            log.error("Failed to process price update", e);
        }
    }

    private void broadcastMessage(String message) {
        sessions.removeIf(session -> !session.isOpen());
        
        for (WebSocketSession session : sessions) {
            try {
                synchronized (session) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(message));
                    }
                }
            } catch (IOException e) {
                log.error("Failed to send message to client: {}", session.getId(), e);
                try {
                    session.close(CloseStatus.PROTOCOL_ERROR);
                } catch (IOException ex) {
                    log.error("Failed to close session", ex);
                }
            }
        }
    }
} 