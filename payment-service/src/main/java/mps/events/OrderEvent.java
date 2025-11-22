package mps.events;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderEvent {
    @JsonProperty("orderId")
    private Long orderId;
    
    @JsonProperty("customerId")
    private Long customerId;
    
    @JsonProperty("productId")
    private String productId;
    
    @JsonProperty("quantity")
    private Integer quantity;
    
    @JsonProperty("totalAmount")
    private Double totalAmount;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    // Default constructor
    public OrderEvent() {}
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}