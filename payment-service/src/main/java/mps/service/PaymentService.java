package mps.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mps.model.Payment;
import mps.repository.PaymentRepository;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
    
    public Payment getPayment(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findAll().stream()
            .filter(p -> p.getOrderId().equals(orderId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }
}