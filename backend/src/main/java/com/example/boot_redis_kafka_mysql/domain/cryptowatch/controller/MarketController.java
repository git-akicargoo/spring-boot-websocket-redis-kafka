package com.example.boot_redis_kafka_mysql.domain.cryptowatch.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.model.Market;
import com.example.boot_redis_kafka_mysql.domain.cryptowatch.service.MarketService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/markets")
@RequiredArgsConstructor
public class MarketController {
    private final MarketService marketService;
    
    @GetMapping("/{exchange}")
    public ResponseEntity<List<Market>> getMarkets(@PathVariable String exchange) {
        return ResponseEntity.ok(marketService.getMarkets(exchange));
    }
} 