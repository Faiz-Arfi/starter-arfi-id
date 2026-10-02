package dev.faizarfi.starter.arfiid.config;

import dev.faizarfi.starter.arfiid.controller.ArfiAuthController;
import dev.faizarfi.starter.arfiid.exception.ArfiExceptionHandler;
import dev.faizarfi.starter.arfiid.security.ArfiAccessDeniedHandler;
import dev.faizarfi.starter.arfiid.security.ArfiAuthenticationEntryPoint;
import dev.faizarfi.starter.arfiid.security.JwtCookieAuthenticationFilter;
import dev.faizarfi.starter.arfiid.service.ArfiAuthServiceClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

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

    @Bean
    @ConditionalOnMissingBean
    public ArfiExceptionHandler arfiExceptionHandler() {
        return new ArfiExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public ArfiAuthenticationEntryPoint arfiAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new ArfiAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ArfiAccessDeniedHandler arfiAccessDeniedHandler(ObjectMapper objectMapper) {
        return new ArfiAccessDeniedHandler(objectMapper);
    }

}