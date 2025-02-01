package com.example.boot_redis_kafka_mysql.domain.cryptowatch.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.dto.PriceDto;
import com.example.boot_redis_kafka_mysql.domain.cryptowatch.model.CoinSymbol;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpbitWebSocketService implements ExchangeWebSocketService {
    
    @Value("${crypto.exchanges.upbit.websocket-url}")
    private String websocketUrl;
    
    @Value("${WS_MAX_REQUESTS_PER_MINUTE:90}")
    private int maxRequestsPerMinute;
    
    @Value("${WS_MIN_MESSAGE_INTERVAL:1000}")
    private long minMessageInterval;
    
    @Value("${WS_LOG_INTERVAL:10}")
    private int logInterval;
    
    private final AtomicInteger minuteRequestCount = new AtomicInteger(0);
    private WebSocketSession webSocketSession;
    private final ObjectMapper objectMapper;
    private final List<PriceListener> priceListeners = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    private volatile long lastMessageTime = 0;
    
    @PostConstruct
    @Override
    public void connect() {
        // 매 분마다 카운터 초기화
        scheduler.scheduleAtFixedRate(() -> 
            minuteRequestCount.set(0), 0, 1, TimeUnit.MINUTES);
        
        // 연결 상태 체크 및 재연결
        scheduler.scheduleAtFixedRate(this::checkConnection, 0, 5, TimeUnit.SECONDS);
                
        connectWebSocket();
    }
    
    private void checkConnection() {
        if (webSocketSession == null || !webSocketSession.isOpen()) {
            log.warn("WebSocket 연결이 끊어졌습니다. 재연결 시도...");
            connectWebSocket();
        }
    }
    
    private void connectWebSocket() {
        try {
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            webSocketClient.execute(new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(@NonNull WebSocketSession session) {
                    webSocketSession = session;
                    log.info("WebSocket 연결 성공");
                    subscribeToSymbols(session);
                }
                
                @Override
                public void handleMessage(@NonNull WebSocketSession session, 
                                        @NonNull WebSocketMessage<?> message) {
                    handleWebSocketMessage(message);
                }
                
                @Override
                public void handleTransportError(@NonNull WebSocketSession session,
                                               @NonNull Throwable exception) {
                    log.error("WebSocket 전송 에러", exception);
                    reconnect();
                }
                
                @Override
                public void afterConnectionClosed(@NonNull WebSocketSession session,
                                                @NonNull CloseStatus status) {
                    log.warn("WebSocket 연결 종료: {}", status);
                    reconnect();
                }
                
                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            }, websocketUrl);
        } catch (Exception e) {
            log.error("WebSocket 연결 실패", e);
            reconnect();
        }
    }
    
    private void reconnect() {
        try {
            Thread.sleep(5000); // 5초 대기
            connectWebSocket();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void subscribeToSymbols(WebSocketSession session) {
        try {
            String symbols = Arrays.toString(CoinSymbol.getAllSymbols());
            String subscribeMessage = String.format(
                "[{\"ticket\":\"UNIQUE_TICKET\"},{\"type\":\"ticker\",\"codes\":%s}]",
                symbols
            );
            session.sendMessage(new BinaryMessage(subscribeMessage.getBytes()));
            log.info("코인 구독 성공: {}", symbols);
        } catch (Exception e) {
            log.error("코인 구독 실패", e);
            reconnect();
        }
    }
    
    private void handleWebSocketMessage(WebSocketMessage<?> message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMessageTime < minMessageInterval) {
            return;
        }
        
        if (minuteRequestCount.incrementAndGet() > maxRequestsPerMinute) {
            if (minuteRequestCount.get() % logInterval == 0) {
                log.debug("분당 요청 제한 초과. 잠시 후 다시 시도합니다.");
            }
            return;
        }
        
        try {
            String payload = new String(((BinaryMessage) message).getPayload().array());
            JsonNode node = objectMapper.readTree(payload);
            
            PriceDto priceDto = PriceDto.builder()
                    .exchange(getExchangeName())
                    .symbol(node.get("code").asText())
                    .price(node.get("trade_price").asDouble())
                    .changeRate(node.get("signed_change_rate").asDouble() * 100)
                    .timestamp(node.get("timestamp").asLong())
                    .build();
            
            lastMessageTime = currentTime;
            notifyListeners(priceDto);
        } catch (Exception e) {
            log.error("메시지 처리 실패", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        scheduler.shutdown();
        disconnect();
    }

    @Override
    public String getExchangeName() {
        return "Upbit";
    }

    @Override
    public void addPriceListener(PriceListener listener) {
        priceListeners.add(listener);
    }

    private void notifyListeners(PriceDto priceDto) {
        priceListeners.forEach(listener -> listener.onPriceUpdate(priceDto));
    }

    @PreDestroy
    @Override
    public void disconnect() {
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close();
            } catch (Exception e) {
                log.error("Failed to close WebSocket session", e);
            }
        }
    }
} 