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
        return paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }
    
    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }
    
    public Payment updatePayment(Long id, Payment payment) {
        Payment existing = getPayment(id);
        if (payment.getAmount() != null) {
            existing.setAmount(payment.getAmount());
        }
        if (payment.getStatus() != null) {
            existing.setStatus(payment.getStatus());
        }
        if (payment.getPaymentMethod() != null) {
            existing.setPaymentMethod(payment.getPaymentMethod());
        }
        return paymentRepository.save(existing);
    }
    
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}