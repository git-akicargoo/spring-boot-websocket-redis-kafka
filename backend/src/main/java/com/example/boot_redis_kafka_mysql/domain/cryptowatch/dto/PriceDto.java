package com.example.boot_redis_kafka_mysql.domain.cryptowatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    private String exchange;    // 거래소 이름 (Upbit, Binance 등)
    private String symbol;      // 거래 쌍 (KRW-BTC 등)
    private Double price;       // 현재 가격
    private Double changeRate;  // 변동률
    private Long timestamp;     // 타임스탬프
} 