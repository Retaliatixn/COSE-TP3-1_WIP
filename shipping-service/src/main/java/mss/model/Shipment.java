package mss.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "shipments")
public class Shipment {
    @Id
    private String id;
    
    private Long orderId;
    private String trackingNumber;
    private String status;
    private String carrier;
    private String address;
    private LocalDateTime shippedDate;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime deliveryDate;
    private LocalDateTime createdAt;
    
    // Constructors
    public Shipment() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public String getTrackingNumber() {
        return trackingNumber;
    }
    
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCarrier() {
        return carrier;
    }
    
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDateTime getShippedDate() {
        return shippedDate;
    }
    
    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }
    
    public LocalDateTime getEstimatedDelivery() {
        return estimatedDelivery;
    }
    
    public void setEstimatedDelivery(LocalDateTime estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
    
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Shipment{" +
                "id='" + id + '\'' +
                ", orderId=" + orderId +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", status='" + status + '\'' +
                ", carrier='" + carrier + '\'' +
                '}';
    }
}