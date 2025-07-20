package io.github.nzuwera.todoapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;

@TestConfiguration
public class WebTestClientConfig {

    @Bean
    public WebTestClient webTestClient() {
        return WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:8080")
                .build();
    }
}