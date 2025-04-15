package com.getinfo.fetch_movie_info.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public String queueName(@Value("${rabbitmq.queue.name}") String name) {
        return name;
    }
}