package com.example.boot_redis_kafka_mysql.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(String username, String content, LocalDateTime timestamp) {} 