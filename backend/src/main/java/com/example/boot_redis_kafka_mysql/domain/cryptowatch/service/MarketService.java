package com.example.boot_redis_kafka_mysql.domain.cryptowatch.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.model.Market;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketService {
    
    private final WebClient webClient;
    
    @Value("${crypto.exchanges.upbit.api.market-all}")
    private String upbitMarketAllUrl;
    
    public List<Market> getMarkets(String exchange) {
        if ("UPBIT".equalsIgnoreCase(exchange)) {
            try {
                List<Market> markets = webClient.get()
                    .uri(upbitMarketAllUrl)
                    .retrieve()
                    .bodyToFlux(Market.class)
                    .filter(market -> market.getMarket().startsWith("KRW-"))
                    .collectList()
                    .block();
                log.info("Fetched {} markets from Upbit", markets != null ? markets.size() : 0);
                return markets;
            } catch (Exception e) {
                log.error("Failed to fetch markets from Upbit", e);
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }
} 