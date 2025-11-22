package com.example.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Order Service
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .uri("http://order-service:8081"))
                
                // Payment Service
                .route("payment-service", r -> r
                        .path("/api/payments/**")
                        .uri("http://payment-service:8084"))
                
                // Shipping Service
                .route("shipping-service", r -> r
                        .path("/api/shipping/**")
                        .uri("http://shipping-service:8085"))
                
                // Notification Service
                .route("notification-service", r -> r
                        .path("/api/notifications/**")
                        .uri("http://notification-service:8086"))
                
                // Customer Service
                .route("customer-service", r -> r
                        .path("/api/customers/**")
                        .uri("http://customer-service:8083"))
                
                // Inventory Service
                .route("inventory-service", r -> r
                        .path("/api/inventory/**")
                        .uri("http://inventory-service:8082"))
                
                .build();
    }
}
