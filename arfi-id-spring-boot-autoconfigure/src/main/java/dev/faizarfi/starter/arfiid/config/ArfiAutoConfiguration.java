package dev.faizarfi.starter.arfiid.config;

import dev.faizarfi.starter.arfiid.controller.ArfiAuthController;
import dev.faizarfi.starter.arfiid.security.JwtCookieAuthenticationFilter;
import dev.faizarfi.starter.arfiid.service.ArfiAuthServiceClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@EnableConfigurationProperties(ArfiProperties.class)
public class ArfiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate arfiRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtCookieAuthenticationFilter jwtCookieAuthenticationFilter(ArfiProperties properties) {
        return new JwtCookieAuthenticationFilter(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ArfiAuthServiceClient arfiAuthServiceClient(ArfiProperties properties, RestTemplate arfiRestTemplate) {
        return new ArfiAuthServiceClient(properties, arfiRestTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public ArfiAuthController arfiAuthController(ArfiAuthServiceClient authServiceClient, ArfiProperties properties) {
        return new ArfiAuthController(authServiceClient, properties);
    }
}