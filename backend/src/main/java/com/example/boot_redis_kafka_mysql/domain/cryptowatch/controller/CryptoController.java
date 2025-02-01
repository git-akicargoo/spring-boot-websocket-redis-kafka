package com.example.boot_redis_kafka_mysql.domain.cryptowatch.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.boot_redis_kafka_mysql.domain.cryptowatch.model.CoinSymbol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/crypto")
@RequiredArgsConstructor
public class CryptoController {

    @GetMapping("/coins")
    public List<CoinInfo> getAvailableCoins() {
        return Arrays.stream(CoinSymbol.values())
                .map(coin -> new CoinInfo(coin.getSymbol(), coin.getKoreanName()))
                .collect(Collectors.toList());
    }
}

@Getter
@AllArgsConstructor
class CoinInfo {
    private String symbol;
    private String koreanName;
} 