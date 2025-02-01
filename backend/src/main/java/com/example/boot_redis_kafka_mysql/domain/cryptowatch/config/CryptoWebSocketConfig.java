package com.example.boot_redis_kafka_mysql.domain.cryptowatch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.controller.CryptoWebSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class CryptoWebSocketConfig implements WebSocketConfigurer {

    @Value("${websocket.allowed-origins}")
    private String allowedOrigins;
    
    @Value("${websocket.endpoint}")
    private String endpoint;
    
    @Value("${WS_MAX_CONNECTIONS_PER_SECOND:4}")
    private int maxConnectionsPerSecond;
    
    @Value("${WS_MAX_REQUESTS_PER_SECOND:4}")
    private int maxRequestsPerSecond;
    
    private final CryptoWebSocketHandler cryptoWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(cryptoWebSocketHandler, endpoint)
                .setAllowedOrigins(allowedOrigins);
    }
} 