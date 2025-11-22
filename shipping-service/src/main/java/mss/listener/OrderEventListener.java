package mss.listener;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import mss.events.OrderEvent;
import mss.model.Shipment;
import mss.repository.ShippingRepository;

@Component
public class OrderEventListener {
    private final ShippingRepository shippingRepository;
    
    public OrderEventListener(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }
    
    @KafkaListener(topics = "order-created", groupId = "shipping-service-group")
    public void handleOrderCreated(OrderEvent event) {
        System.out.println("Shipping service received order: " + event.getOrderId());
        
        // Create shipment
        Shipment shipment = new Shipment();
        shipment.setOrderId(event.getOrderId());
        shipment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        shipment.setStatus("PENDING");
        shipment.setShippedDate(LocalDateTime.now());
        
        shippingRepository.save(shipment);
        
        System.out.println("Shipment created for order: " + event.getOrderId());
    }
}