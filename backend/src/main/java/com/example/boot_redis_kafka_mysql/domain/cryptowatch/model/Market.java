package com.example.boot_redis_kafka_mysql.domain.cryptowatch.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Market {
    private String market;      // 마켓 코드 (예: KRW-BTC)
    
    @JsonProperty("korean_name")
    private String koreanName;  // 한글 이름 (예: 비트코인)
    
    @JsonProperty("english_name")
    private String englishName; // 영문 이름 (예: Bitcoin)
    
    @JsonProperty("market_warning")
    private String marketWarning;
    
    private String marketGroup; // 마켓 그룹 (예: KRW, BTC, USDT)
    private Exchange exchange;  // 거래소 정보
    private Double currentPrice;// 현재가
    private Double changeRate; // 변동률
    private Long timestamp;    // 타임스탬프
} 