package com.example.boot_redis_kafka_mysql.domain.cryptowatch.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.model.Exchange;

@Service
public class ExchangeService {
    
    @Value("${crypto.exchanges.upbit.websocket-url}")
    private String upbitWsUrl;
    
    public List<Exchange> getExchanges() {
        return Arrays.asList(
            new Exchange(
                "UPBIT",
                "업비트",
                upbitWsUrl,
                "KRW",
                true,
                Arrays.asList("KRW-BTC", "KRW-DOGE")
            )
        );
    }
} 