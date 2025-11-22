package mps.listener;

import mps.events.OrderEvent;
import mps.model.Payment;
import mps.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OrderEventListener {
    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);
    private final PaymentRepository paymentRepository;
    
    public OrderEventListener(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void handleOrderCreated(OrderEvent event) {
        log.info("Payment service received order event: {}", event.getOrderId());
        
        try {
            // Check if payment already exists
            if (paymentRepository.findByOrderId(event.getOrderId()).isPresent()) {
                log.warn("Payment already exists for order: {}", event.getOrderId());
                return;
            }
            
            // Create payment
            Payment payment = new Payment();
            payment.setOrderId(event.getOrderId());
            payment.setAmount(event.getTotalAmount());
            payment.setStatus("COMPLETED");
            payment.setPaymentMethod("CREDIT_CARD");
            payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
            payment.setPaymentDate(LocalDateTime.now());
            
            payment = paymentRepository.save(payment);
            
            log.info("Payment processed successfully: {} for order: {} with transaction: {}", 
                    payment.getId(), event.getOrderId(), payment.getTransactionId());
            
        } catch (Exception e) {
            log.error("Error processing payment for order {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
}