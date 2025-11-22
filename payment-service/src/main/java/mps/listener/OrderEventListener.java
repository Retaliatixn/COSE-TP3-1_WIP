package mps.listener;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import mps.events.OrderEvent;
import mps.model.Payment;
import mps.repository.PaymentRepository;

@Component
public class OrderEventListener {
    private final PaymentRepository paymentRepository;
    
    public OrderEventListener(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void handleOrderCreated(OrderEvent event) {
        System.out.println("Payment service received order : " + event.getOrderId());
        
        // Process payment
        Payment payment = new Payment();
        payment.setOrderId(event.getOrderId());
        payment.setAmount(event.getTotalAmount());
        payment.setStatus("COMPLETED");
        payment.setPaymentDate(LocalDateTime.now());
        
        paymentRepository.save(payment);
        
        System.out.println("Payment processed for order : " + event.getOrderId());
    }
}