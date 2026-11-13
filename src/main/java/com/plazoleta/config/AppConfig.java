package com.plazoleta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Objetos de ayuda que Spring crea una sola vez y entrega a quien los pida.
 */
@Configuration
public class AppConfig {

    // RestTemplate = herramienta para llamar a otras APIs por internet (HU-14: Twilio)
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
