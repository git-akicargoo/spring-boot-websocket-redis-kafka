package com.example.boot_redis_kafka_mysql.domain.cryptowatch.service;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.dto.PriceDto;

public interface ExchangeWebSocketService {
    void connect();
    void disconnect();
    String getExchangeName();
    void addPriceListener(PriceListener listener);
    
    interface PriceListener {
        void onPriceUpdate(PriceDto priceDto);
    }
} 