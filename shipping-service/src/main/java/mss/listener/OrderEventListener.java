package mss.listener;

import mss.events.OrderEvent;
import mss.model.Shipment;
import mss.repository.ShippingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final ShippingRepository shippingRepository;
    
    public OrderEventListener(ShippingRepository shippingRepository) {
        this.shippingRepository = shippingRepository;
    }
    
    @KafkaListener(topics = "order-created", groupId = "shipping-service-group")
    public void handleOrderCreated(OrderEvent event) {
        log.info("Shipping service received order event: {}", event.getOrderId());
        
        try {
            // Check if shipment already exists
            if (shippingRepository.findByOrderId(event.getOrderId()).isPresent()) {
                log.warn("Shipment already exists for order: {}", event.getOrderId());
                return;
            }
            
            // Create shipment
            Shipment shipment = new Shipment();
            shipment.setOrderId(event.getOrderId());
            shipment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
            shipment.setStatus("PROCESSING");
            shipment.setCarrier("FastShip Express");
            shipment.setAddress("Customer Address (from customer service)");
            shipment.setShippedDate(LocalDateTime.now());
            shipment.setEstimatedDelivery(LocalDateTime.now().plusDays(3));
            
            shipment = shippingRepository.save(shipment);
            
            log.info("Shipment created successfully: {} for order: {} with tracking: {}", 
                    shipment.getId(), event.getOrderId(), shipment.getTrackingNumber());
            
        } catch (Exception e) {
            log.error("Error creating shipment for order {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}