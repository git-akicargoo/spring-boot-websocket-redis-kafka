package com.example.boot_redis_kafka_mysql.domain.cryptowatch.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    private WebSocketSession webSocketSession;
    private final ObjectMapper objectMapper;
    private final List<PriceListener> priceListeners = new ArrayList<>();
    private final StringBuilder connectionLog = new StringBuilder();
    
    @PostConstruct
    @Override
    public void connect() {
        try {
            connectionLog.append("\n=== 🔌 Upbit WebSocket 연결 정보 ===\n");
            connectionLog.append(String.format("🌐 접속 URL: %s\n", websocketUrl));
            
            WebSocketClient webSocketClient = new StandardWebSocketClient();
            WebSocketHandler webSocketHandler = new WebSocketHandler() {
                @Override
                public void afterConnectionEstablished(@NonNull WebSocketSession session) {
                    webSocketSession = session;
                    connectionLog.append(String.format("✅ 연결 성공 (Session ID: %s)\n", session.getId()));
                    
                    try {
                        // 모든 코인에 대한 구독 메시지 생성
                        String symbols = Arrays.toString(CoinSymbol.getAllSymbols());
                        connectionLog.append(String.format("📋 구독할 코인: %s\n", symbols));
                        
                        String subscribeMessage = String.format(
                            "[{\"ticket\":\"test\"},{\"type\":\"ticker\",\"codes\":%s,\"isOnlyRealtime\":true}]",
                            symbols
                        );
                        
                        connectionLog.append("📡 구독 요청 전송\n");
                        connectionLog.append(String.format("📨 요청 내용: %s\n", subscribeMessage));
                        session.sendMessage(new BinaryMessage(subscribeMessage.getBytes()));
                        connectionLog.append("✅ 구독 요청 완료\n");
                    } catch (Exception e) {
                        connectionLog.append(String.format("❌ 구독 요청 실패: %s\n", e.getMessage()));
                    }
                    
                    log.info(connectionLog.toString());
                }

                @Override
                public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {
                    StringBuilder priceLog = new StringBuilder();
                    priceLog.append("\n=== 💰 실시간 가격 정보 ===\n");
                    
                    try {
                        String payload = new String(((BinaryMessage) message).getPayload().array());
                        JsonNode node = objectMapper.readTree(payload);
                        String symbol = node.get("code").asText();
                        
                        // 코인 한글명 찾기
                        String coinName = Arrays.stream(CoinSymbol.values())
                                .filter(coin -> coin.getSymbol().equals(symbol))
                                .findFirst()
                                .map(CoinSymbol::getKoreanName)
                                .orElse(symbol);
                        
                        PriceDto priceDto = PriceDto.builder()
                                .exchange(getExchangeName())
                                .symbol(symbol)
                                .price(node.get("trade_price").asDouble())
                                .changeRate(node.get("signed_change_rate").asDouble() * 100)
                                .timestamp(node.get("timestamp").asLong())
                                .build();
                        
                        // 간단한 가격 정보만 로깅
                        priceLog.append(String.format("💵 %s: %s (%.2f%%)\n", 
                            coinName,
                            String.format("%,d", priceDto.getPrice().longValue()),
                            priceDto.getChangeRate()));
                        priceLog.append(String.format("⏰ %s\n", 
                            new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(priceDto.getTimestamp()))));
                        
                        log.info(priceLog.toString());
                        notifyListeners(priceDto);
                    } catch (Exception e) {
                        log.error("❌ 가격 데이터 처리 실패", e);
                    }
                }

                @Override
                public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {
                    log.error("❌ WebSocket 전송 에러: {}", exception.getMessage());
                }

                @Override
                public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
                    log.info("\n=== 🔌 WebSocket 연결 종료 ===\n상태: {}", status);
                }

                @Override
                public boolean supportsPartialMessages() {
                    return false;
                }
            };
            
            webSocketClient.execute(webSocketHandler, websocketUrl).get();
            
        } catch (Exception e) {
            connectionLog.append(String.format("❌ 연결 실패: %s\n", e.getMessage()));
            log.error(connectionLog.toString());
        }
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
} 