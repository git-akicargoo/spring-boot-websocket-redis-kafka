package com.example.boot_redis_kafka_mysql.domain.cryptowatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "crypto.features")
public class CryptoFeatureConfig {
    private FeatureProperties redis = new FeatureProperties();
    private FeatureProperties kafka = new FeatureProperties();

    @Getter
    @Setter
    public static class FeatureProperties {
        private boolean enabled;
    }
} 