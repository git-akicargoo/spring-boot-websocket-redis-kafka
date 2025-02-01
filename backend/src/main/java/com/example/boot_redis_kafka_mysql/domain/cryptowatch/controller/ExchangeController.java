package com.example.boot_redis_kafka_mysql.domain.cryptowatch.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.model.Exchange;
import com.example.boot_redis_kafka_mysql.domain.cryptowatch.service.ExchangeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeController {
    
    private final ExchangeService exchangeService;
    
    @GetMapping
    public ResponseEntity<List<Exchange>> getExchanges() {
        return ResponseEntity.ok(exchangeService.getExchanges());
    }
} 