package com.example.boot_redis_kafka_mysql.domain.cryptowatch.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exchange {
    private String id;              // 거래소 고유 ID (예: UPBIT, BINANCE)
    private String name;            // 거래소 이름 (예: 업비트, 바이낸스)
    private String websocketUrl;    // 웹소켓 연결 URL
    private String currency;        // 기준 통화 (KRW, USD)
    private boolean active;         // 활성화 상태
    private List<String> defaultMarkets; // 기본 마켓 목록
} 