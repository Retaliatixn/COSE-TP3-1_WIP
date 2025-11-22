package mns.listener;

import mns.events.OrderEvent;
import mns.model.Notification;
import mns.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final NotificationRepository notificationRepository;
    
    public OrderEventListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @KafkaListener(topics = "order-created", groupId = "notification-service-group")
    public void handleOrderCreated(OrderEvent event) {
        log.info("Notification service received order event: {}", event.getOrderId());
        
        try {
            // Create notification
            Notification notification = new Notification();
            notification.setOrderId(event.getOrderId());
            notification.setCustomerId(event.getCustomerId());
            notification.setMessage(String.format(
                "Your order #%d has been created successfully! Total amount: $%.2f. " +
                "We'll notify you once it ships. Thank you for your purchase!",
                event.getOrderId(),
                event.getTotalAmount()
            ));
            notification.setType("ORDER_CREATED");
            notification.setStatus("SENT");
            notification.setChannel("EMAIL");
            
            notification = notificationRepository.save(notification);
            
            log.info("Notification sent successfully: {} for order: {} to customer: {}", 
                    notification.getId(), event.getOrderId(), event.getCustomerId());
            
        } catch (Exception e) {
            log.error("Error sending notification for order {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}