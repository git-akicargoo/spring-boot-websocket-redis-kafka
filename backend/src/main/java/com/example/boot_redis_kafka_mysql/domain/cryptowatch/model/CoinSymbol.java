package com.example.boot_redis_kafka_mysql.domain.cryptowatch.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoinSymbol {
    BTC("KRW-BTC", "비트코인"),
    DOGE("KRW-DOGE", "도지코인");
    
    private final String symbol;      // 업비트 심볼
    private final String koreanName;  // 한글 이름
    
    public static String[] getAllSymbols() {
        return java.util.Arrays.stream(values())
                .map(CoinSymbol::getSymbol)
                .toArray(String[]::new);
    }
} 