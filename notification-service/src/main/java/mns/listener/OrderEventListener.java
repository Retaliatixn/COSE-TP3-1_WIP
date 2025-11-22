package mns.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import mns.events.OrderEvent;
import mns.model.Notification;
import mns.repository.NotificationRepository;

@Component
public class OrderEventListener {
    private final NotificationRepository notificationRepository;
    
    public OrderEventListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @KafkaListener(topics = "order-created", groupId = "notification-service-group")
    public void handleOrderCreated(OrderEvent event) {
        System.out.println("Notification service received order: " + event.getOrderId());
        
        // Create notification
        Notification notification = new Notification();
        notification.setOrderId(event.getOrderId());
        notification.setCustomerId(event.getCustomerId());
        notification.setMessage("Your order #" + event.getOrderId() + " has been created successfully. Total: $" + event.getTotalAmount());
        notification.setType("ORDER_CREATED");
        notification.setStatus("SENT");
        
        notificationRepository.save(notification);
        
        System.out.println("Notification sent for order: " + event.getOrderId());
    }
}