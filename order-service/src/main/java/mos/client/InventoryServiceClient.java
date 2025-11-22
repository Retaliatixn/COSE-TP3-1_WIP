package mos.client;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class InventoryServiceClient {
    private static final Logger log = LoggerFactory.getLogger(InventoryServiceClient.class);
    private final WebClient webClient;
    
    public InventoryServiceClient(WebClient.Builder webClientBuilder,
                                 @Value("${services.inventory.url:http://localhost:8082}") String inventoryServiceUrl) {
        this.webClient = webClientBuilder
            .baseUrl(inventoryServiceUrl)
            .build();
    }
    
    public ProductInfo checkInventory(String productId, Integer quantity) {
        try {
            log.info("Checking inventory for product {}, quantity {}", productId, quantity);
            
            ProductResponse response = webClient.get()
                .uri("/api/inventory/{id}", productId)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .timeout(Duration.ofSeconds(5))
                .block();
            
            if (response == null) {
                log.error("Product {} not found", productId);
                return new ProductInfo(false, 0.0);
            }
            
            boolean available = response.getQuantity() >= quantity;
            log.info("Product {} available: {}, quantity: {}, price: {}", 
                    productId, available, response.getQuantity(), response.getPrice());
            
            return new ProductInfo(available, response.getPrice());
            
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Product {} not found", productId);
            return new ProductInfo(false, 0.0);
        } catch (Exception e) {
            log.error("Error checking inventory for product {}: {}", productId, e.getMessage());
            return new ProductInfo(false, 0.0);
        }
    }
    
    // Inner class for product info result
    public static class ProductInfo {
        private final boolean available;
        private final Double price;
        
        public ProductInfo(boolean available, Double price) {
            this.available = available;
            this.price = price;
        }
        
        public boolean isAvailable() {
            return available;
        }
        
        public Double getPrice() {
            return price;
        }
    }
    
    // Inner class for deserializing product response
    static class ProductResponse {
        private String id;  // MongoDB uses String IDs
        private String name;
        private Integer quantity;
        private Double price;
        
        public ProductResponse() {}
        
        public String getId() {
            return id;
        }
        
        public void setId(String id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public Double getPrice() {
            return price;
        }
        
        public void setPrice(Double price) {
            this.price = price;
        }
    }
}