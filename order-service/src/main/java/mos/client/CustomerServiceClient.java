package mos.client;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CustomerServiceClient {
    private static final Logger log = LoggerFactory.getLogger(CustomerServiceClient.class);
    private final WebClient webClient;
    
    public CustomerServiceClient(WebClient.Builder webClientBuilder,
                                @Value("${services.customer.url:http://localhost:8083}") String customerServiceUrl) {
        this.webClient = webClientBuilder
            .baseUrl(customerServiceUrl)
            .build();
    }
    
    public boolean customerExists(Long customerId) {
        try {
            log.info("Checking if customer exists: {}", customerId);
            
            webClient.get()
                .uri("/api/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            log.info("Customer {} exists", customerId);
            return true;
            
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Customer {} not found", customerId);
            return false;
        } catch (Exception e) {
            log.error("Error checking customer {}: {}", customerId, e.getMessage());
            return false;
        }
    }
}